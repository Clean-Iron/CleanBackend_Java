package co.cleaniron.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class ScheduleDetailGroupedDto {
    private Long id;
    private String clientDocument;
    private LocalDate serviceDate;
    private LocalTime startDate;
    private LocalTime endDate;
    private double totalServiceHours;
    private String state;
    private String comments;
    private String clientName;
    private String clientSurname;
    private String clientCompleteName;
    private String city;
    private String addressService;
    private Set<ServiceDto> services;
    private Set<EmployeeDto> employees;

    public ScheduleDetailGroupedDto() {}

    public ScheduleDetailGroupedDto(ScheduleDetailDateDto dto) {
        this.id = dto.getId();
        this.clientDocument = dto.getClientDocument();
        this.serviceDate = dto.getServiceDate();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.totalServiceHours = dto.getTotalServiceHours();
        this.state = dto.getState();
        this.comments = dto.getComments();
        this.clientName = dto.getClientName();
        this.clientSurname = dto.getClientSurname();
        this.city = dto.getCity();
        this.clientCompleteName = dto.getNombreCompletoCliente();
        this.addressService = dto.getAddressService();
    }
}
