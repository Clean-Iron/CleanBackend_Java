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
        Integer breakMinutes,              // ← NUEVO: descanso en minutos (0–60)
        Set<String> employeeDocuments,
        Set<Integer> idServices
) {
    public ScheduleUpdateDto {
        // Normalizaciones
        employeeDocuments = employeeDocuments == null ? Set.of() : Set.copyOf(employeeDocuments);
        idServices = idServices == null ? Set.of() : Set.copyOf(idServices);

        // Validación de horas
        if (startHour != null && endHour != null && !endHour.isAfter(startHour)) {
            throw new IllegalArgumentException("endHour debe ser posterior a startHour");
        }

        // Descanso: default 0 y clamp al rango [0,60]
        if (breakMinutes == null) breakMinutes = 0;
        if (breakMinutes < 0) breakMinutes = 0;
        if (breakMinutes > 60) breakMinutes = 60;
    }
}
