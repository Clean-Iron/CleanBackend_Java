package co.cleaniron.service;

import co.cleaniron.model.Schedule;
import co.cleaniron.model.dto.*;
import co.cleaniron.repository.EmployeeRepository;
import co.cleaniron.repository.ScheduleRepository;
import co.cleaniron.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
