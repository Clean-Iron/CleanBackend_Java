package co.cleaniron.model.dto;

import java.time.LocalDate;

public record DayHoursDto(
        LocalDate date,
        Double hours
) {}

