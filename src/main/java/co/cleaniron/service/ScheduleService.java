package co.cleaniron.service;

import co.cleaniron.model.Schedule;
import co.cleaniron.model.dto.*;
import co.cleaniron.repository.EmployeeRepository;
import co.cleaniron.repository.ScheduleRepository;
import co.cleaniron.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public void createSchedule(Schedule schedule){
        scheduleRepository.save(schedule);
    }

    public List<Object[]> getServicesFromEmployeeByMonth(String doc, LocalDate date){
        return scheduleRepository.findServicesFromEmployeeByMonth(doc, date);
    }

    public List<ScheduleDetailGroupedDto> getScheduleDetailsByDateCityClient(LocalDate date, String city, String name, String surname) {
        // Obtener todos los registros como antes
        List<Object[]> results = scheduleRepository.findScheduleDetailsByDateCityClient(date, city, name, surname);
        List<ScheduleDetailDateDto> scheduleDetails = results.stream()
                .map(ScheduleDetailDateDto::new)
                .toList();

        // Agrupar por ID de agenda y combinar empleados
        Map<Long, List<ScheduleDetailDateDto>> groupedByScheduleId = scheduleDetails.stream()
                .collect(Collectors.groupingBy(ScheduleDetailDateDto::getId));

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
                .collect(Collectors.groupingBy(ScheduleDetailDateDto::getId));

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
                .filter(s -> s.getEmployeeDocument() != null) // Filtrar empleados nulos
                .map(s -> new EmployeeDto(s.getEmployeeDocument(), s.getEmployeeName(), s.getEmployeeSurname()))
                .collect(Collectors.toSet());

        Set<ServiceDto> services = scheduleGroup.stream()
                .filter(s -> s.getIdService() != null) // Filtrar empleados nulos
                .map(s -> new ServiceDto(s.getIdService(), s.getServiceDescription()))
                .collect(Collectors.toSet());

        grouped.setServices(services);
        grouped.setEmployees(employees);
        return grouped;
    }

    public Schedule updateSchedulePartial(Long id, ScheduleUpdateDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda no encontrada con ID: " + id));

        if (dto.getDate() != null) {
            schedule.setDate(dto.getDate());
        }

        if (dto.getStartHour() != null) {
            schedule.setStartHour(dto.getStartHour());
        }

        if (dto.getEndHour() != null) {
            schedule.setEndHour(dto.getEndHour());
        }

        if (dto.getComments() != null) {
            schedule.setComments(dto.getComments());
        }

        if (dto.getState() != null) {
            schedule.setState(dto.getState());
        }

        if (dto.getEmployeeDocuments() != null) {
            schedule.setEmployees(
                    new HashSet<>(employeeRepository.findAllById(dto.getEmployeeDocuments()))
            );
        }

        if (dto.getIdServices() != null) {
            schedule.setServices(
                    new HashSet<>(serviceRepository.findAllById(dto.getIdServices()))
            );
        }

        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id){
        scheduleRepository.deleteById(id);
    }
}
