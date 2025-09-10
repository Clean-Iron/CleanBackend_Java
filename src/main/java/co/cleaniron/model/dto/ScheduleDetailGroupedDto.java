package co.cleaniron.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Setter
@Getter
public class ScheduleDetailGroupedDto {
    private Long id;
    private String clientDocument;
    private LocalDate serviceDate;
    private LocalTime startHour;
    private LocalTime endHour;
    private double totalServiceHours;
    private String state;
    private String comments;
    private String clientName;
    private String clientSurname;
    private String clientCompleteName;
    private String city;
    private String addressService;
    private String recurrenceType;
    private Set<ServiceDto> services;
    private Set<EmployeeDto> employees;

    public ScheduleDetailGroupedDto() {}

    public ScheduleDetailGroupedDto(ScheduleDetailDateDto dto) {
        this.id = dto.id();
        this.clientDocument = dto.clientDocument();
        this.serviceDate = dto.serviceDate();
        this.startHour = dto.startHour();
        this.endHour = dto.endHour();
        this.totalServiceHours = dto.totalServiceHours();
        this.state = dto.state();
        this.comments = dto.comments();
        this.clientName = dto.clientName();
        this.clientSurname = dto.clientSurname();
        this.city = dto.city();
        this.clientCompleteName = dto.nombreCompletoCliente();
        this.addressService = dto.addressService();
        this.recurrenceType = dto.recurrenceType();
    }
}
