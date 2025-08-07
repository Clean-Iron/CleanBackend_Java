package co.cleaniron.model.dto;

import co.cleaniron.model.ServiceState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class ScheduleUpdateDto {
    private LocalDate date;
    private LocalTime startHour;
    private LocalTime endHour;
    private String comments;
    private ServiceState state;
    private Set<String> employeeDocuments;
    private Set<Integer> idServices;
}

