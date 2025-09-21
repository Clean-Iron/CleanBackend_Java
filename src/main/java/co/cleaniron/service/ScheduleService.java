package co.cleaniron.service;

import co.cleaniron.model.Schedule;
import co.cleaniron.model.dto.*;
import co.cleaniron.repository.EmployeeRepository;
import co.cleaniron.repository.ScheduleRepository;
import co.cleaniron.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository, ServiceRepository serviceRepository, EmployeeRepository employeeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.serviceRepository = serviceRepository;
        this.employeeRepository = employeeRepository;
    }

    public void createSchedule(List<Schedule> schedules) {
        for (Schedule s : schedules) {
            scheduleRepository.save(s);
        }
    }

    public List<ScheduleDetailGroupedDto> getServicesFromEmployeeByMonth(String doc, String year, String month) {
        List<Object[]> results = scheduleRepository.findServicesFromEmployeeByMonth(doc, year, month);
        List<ScheduleDetailDateDto> scheduleDetails = results.stream()
                .map(ScheduleDetailDateDto::new)
                .toList();

        // Agrupar por ID de agenda y combinar empleados
        Map<Long, List<ScheduleDetailDateDto>> groupedByScheduleId = scheduleDetails.stream()
                .collect(Collectors.groupingBy(ScheduleDetailDateDto::id));

        // Convertir a DTO agrupado
        return groupedByScheduleId.values().stream()
                .map(this::combineScheduleWithEmployees)
                .collect(Collectors.toList());
    }

    public List<ScheduleDetailGroupedDto> getServicesFromClientByMonth(String doc, String year, String month) {
        List<Object[]> results = scheduleRepository.findServicesFromClientByMonth(doc, year, month);
        List<ScheduleDetailDateDto> scheduleDetails = results.stream()
                .map(ScheduleDetailDateDto::new)
                .toList();

        // Agrupar por ID de agenda y combinar empleados
        Map<Long, List<ScheduleDetailDateDto>> groupedByScheduleId = scheduleDetails.stream()
                .collect(Collectors.groupingBy(ScheduleDetailDateDto::id));

        // Convertir a DTO agrupado
        return groupedByScheduleId.values().stream()
                .map(this::combineScheduleWithEmployees)
                .collect(Collectors.toList());
    }

    public List<ScheduleDetailGroupedDto> getServicesFromCityByMonth(String city, String year, String month) {
        List<Object[]> results = scheduleRepository.findServicesByCityAndMonth(city, year, month);
        List<ScheduleDetailDateDto> scheduleDetails = results.stream()
                .map(ScheduleDetailDateDto::new)
                .toList();

        // Agrupar por ID de agenda y combinar empleados
        Map<Long, List<ScheduleDetailDateDto>> groupedByScheduleId = scheduleDetails.stream()
                .collect(Collectors.groupingBy(ScheduleDetailDateDto::id));

        // Convertir a DTO agrupado
        return groupedByScheduleId.values().stream()
                .map(this::combineScheduleWithEmployees)
                .collect(Collectors.toList());
    }

    public List<ScheduleDetailGroupedDto> getScheduleDetailsByDateCityClient(LocalDate date, String city, String name, String surname) {
        // Obtener todos los registros como antes
        List<Object[]> results = scheduleRepository.findScheduleDetailsByDateCityClient(date, city, name, surname);
        List<ScheduleDetailDateDto> scheduleDetails = results.stream()
                .map(ScheduleDetailDateDto::new)
                .toList();

        // Agrupar por ID de agenda y combinar empleados
        Map<Long, List<ScheduleDetailDateDto>> groupedByScheduleId = scheduleDetails.stream()
                .collect(Collectors.groupingBy(ScheduleDetailDateDto::id));

        // Convertir a DTO agrupado
        return groupedByScheduleId.values().stream()
                .map(this::combineScheduleWithEmployees)
                .collect(Collectors.toList());
    }

    public List<ScheduleDetailGroupedDto> getScheduleDetailsByDate(LocalDate date) {
        // Obtener todos los registros como antes
        List<Object[]> results = scheduleRepository.findScheduleDetailsByDate(date);
        List<ScheduleDetailDateDto> scheduleDetails = results.stream()
                .map(ScheduleDetailDateDto::new)
                .toList();

        // Agrupar por ID de agenda y combinar empleados
        Map<Long, List<ScheduleDetailDateDto>> groupedByScheduleId = scheduleDetails.stream()
                .collect(Collectors.groupingBy(ScheduleDetailDateDto::id));

        // Convertir a DTO agrupado
        return groupedByScheduleId.values().stream()
                .map(this::combineScheduleWithEmployees)
                .collect(Collectors.toList());
    }

    public List<EmployeeDailyHoursGroupedDto> getScheduleEmployeesByMonth(String year, String month) {
        List<Object[]> rows = scheduleRepository.findServicesFromEmployeesByMonth(year, month);
        return groupEmployeeDailyHours(rows);
    }

    // === PRIVADO: agrupa {doc(String), nombre(String), fecha(LocalDate), horas(Double)} ===
    private List<EmployeeDailyHoursGroupedDto> groupEmployeeDailyHours(List<Object[]> rows) {
        // Agrupar por documento (preserva el orden de llegada)
        Map<String, List<Object[]>> byDoc = rows.stream()
                .collect(Collectors.groupingBy(
                        r -> (String) r[0],
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Construir la lista final (un empleado con su lista de días/horas)
        return byDoc.entrySet().stream()
                .map(entry -> {
                    String doc = entry.getKey();
                    List<Object[]> empRows = entry.getValue();

                    String name = (String) empRows.get(0)[1];

                    // Consolidar horas por fecha (si por alguna razón hay varias filas mismo día, se suman)
                    Map<LocalDate, Double> hoursByDate = new TreeMap<>(); // fechas ordenadas asc
                    for (Object[] r : empRows) {
                        LocalDate date = (LocalDate) r[2];
                        Double hours   = (Double) r[3];
                        hoursByDate.merge(date, hours, Double::sum);
                    }

                    List<DayHoursDto> days = hoursByDate.entrySet().stream()
                            .map(e -> new DayHoursDto(e.getKey(), e.getValue()))
                            .toList();

                    return new EmployeeDailyHoursGroupedDto(doc, name, days);
                })
                // (Opcional) ordenar por nombre
                .sorted(Comparator.comparing(EmployeeDailyHoursGroupedDto::employeeName,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    private ScheduleDetailGroupedDto combineScheduleWithEmployees(List<ScheduleDetailDateDto> scheduleGroup) {
        // Tomar el primer registro como base (todos tienen la misma info del servicio)
        ScheduleDetailDateDto base = scheduleGroup.get(0);
        ScheduleDetailGroupedDto grouped = new ScheduleDetailGroupedDto(base);

        // Combinar todos los empleados únicos
        Set<EmployeeDto> employees = scheduleGroup.stream()
                .filter(s -> s.employeeDocument() != null) // Filtrar empleados nulos
                .map(s -> new EmployeeDto(s.employeeDocument(), s.employeeName(), s.employeeSurname()))
                .collect(Collectors.toSet());

        Set<ServiceDto> services = scheduleGroup.stream()
                .filter(s -> s.idService() != null) // Filtrar empleados nulos
                .map(s -> new ServiceDto(s.idService(), s.serviceDescription()))
                .collect(Collectors.toSet());

        grouped.setServices(services);
        grouped.setEmployees(employees);
        return grouped;
    }

    public Schedule updateSchedulePartial(Long id, ScheduleUpdateDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda no encontrada con ID: " + id));

        if (dto.date() != null) {
            schedule.setDate(dto.date());
        }

        if (dto.startHour() != null) {
            schedule.setStartHour(dto.startHour());
        }

        if (dto.endHour() != null) {
            schedule.setEndHour(dto.endHour());
        }

        if (dto.comments() != null) {
            schedule.setComments(dto.comments());
        }

        if (dto.state() != null) {
            schedule.setState(dto.state());
        }

        if (dto.breakMinutes() != null) {
            schedule.setBreakMinutes(dto.breakMinutes());
        }

        if (dto.employeeDocuments() != null) {
            schedule.setEmployees(
                    new HashSet<>(employeeRepository.findAllById(dto.employeeDocuments()))
            );
        }

        if (dto.idServices() != null) {
            schedule.setServices(
                    new HashSet<>(serviceRepository.findAllById(dto.idServices()))
            );
        }

        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
