package co.cleaniron.model.dto;

import java.time.LocalDate;

public record DayHoursDto(
        String client,
        LocalDate date,
        Double hours
) {
}