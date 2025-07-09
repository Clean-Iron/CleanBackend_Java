package co.cleaniron.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
public class ScheduleDetailGroupedDto {
    // Getters y setters
    private Long id;
    private Integer clientDocument;
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
    private List<String> serviceDescription;
    private List<EmployeeDto> employees;

    public ScheduleDetailGroupedDto() {}

    // Constructor desde ScheduleDetailDateDto
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
