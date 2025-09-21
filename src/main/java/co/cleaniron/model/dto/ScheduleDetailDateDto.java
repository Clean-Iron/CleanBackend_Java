package co.cleaniron.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleDetailDateDto(
        Long id,
        String clientDocument,
        LocalDate serviceDate,
        LocalTime startHour,
        LocalTime endHour,
        Integer breakMinutes,
        Double totalServiceHours,
        String state,
        String comments,
        String recurrenceType,
        String clientName,
        String clientSurname,
        String city,
        String addressService,
        Integer idService,
        String serviceDescription,
        String employeeDocument,
        String employeeName,
        String employeeSurname
) {

    // Constructor adicional que recibe el Object[]
    public ScheduleDetailDateDto(Object[] row) {
        this(
                castToLong(row[0]),
                castToString(row[1]),
                castToLocalDate(row[2]),
                castToLocalTime(row[3]),
                castToLocalTime(row[4]),
                castToInteger(row[5]),
                castToDouble(row[6]),
                castToString(row[7]),
                castToString(row[8]),
                castToString(row[9]),
                castToString(row[10]),
                castToString(row[11]),
                castToString(row[12]),
                castToString(row[13]),
                castToInteger(row[14]),
                castToString(row[15]),
                castToString(row[16]),
                castToString(row[17]),
                castToString(row[18])
        );
    }

    // Helpers estáticos
    private static Double castToDouble(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Double) return (Double) obj;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long castToLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Number) return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer castToInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String castToString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private static LocalDate castToLocalDate(Object obj) {
        if (obj == null) return null;
        if (obj instanceof java.sql.Date) return ((java.sql.Date) obj).toLocalDate();
        if (obj instanceof LocalDate) return (LocalDate) obj;
        try {
            return LocalDate.parse(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private static LocalTime castToLocalTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof java.sql.Time) return ((java.sql.Time) obj).toLocalTime();
        if (obj instanceof LocalTime) return (LocalTime) obj;
        try {
            return LocalTime.parse(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    // Método adicional para el nombre completo del cliente
    public String nombreCompletoCliente() {
        if (clientName == null && clientSurname == null) return null;
        return (clientName != null ? clientName : "") + " " +
                (clientSurname != null ? clientSurname : "").trim();
    }
}
