package co.cleaniron.model.dto;

import co.cleaniron.model.ServiceState;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScheduleUpdateDto(
        LocalDate date,
        @JsonFormat(pattern = "HH:mm:ss") LocalTime startHour,
        @JsonFormat(pattern = "HH:mm:ss") LocalTime endHour,
        String comments,
        ServiceState state,
        Set<String> employeeDocuments,
        Set<Integer> idServices
) {
    // Compact constructor: normaliza y valida
    public ScheduleUpdateDto {
        employeeDocuments = employeeDocuments == null ? Set.of() : Set.copyOf(employeeDocuments);
        idServices = idServices == null ? Set.of() : Set.copyOf(idServices);

        if (startHour != null && endHour != null && !endHour.isAfter(startHour)) {
            throw new IllegalArgumentException("endHour debe ser posterior a startHour");
        }
    }
}
