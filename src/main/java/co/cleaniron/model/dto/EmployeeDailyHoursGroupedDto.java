package co.cleaniron.model.dto;

import java.util.List;

public record EmployeeDailyHoursGroupedDto(
        String employeeDocument,
        String employeeName,
        List<DayHoursDto> days
) {
}

