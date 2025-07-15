package co.cleaniron.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicesByEmployeeDto {
    private Long id;
    private String clientDocument;
    private LocalDate serviceDate;
    private LocalTime startHour;
    private LocalTime endHour;
    private Double totalServiceHours;
    private String comments;
    private String clientName;
    private String clientSurname;
    private String addressService;
    private Long idService;
    private String serviceDescription;
    private String employeeDocument;
    private String employeeName;
    private String employeeSurname;

    // Constructor con casting correcto según tus entidades
    public ServicesByEmployeeDto(Object[] row) {
        this.id = castToLong(row[0]);                          // ID de la agenda
        this.clientDocument = castToString(row[1]);            // NUMERO_DOCUMENTO es Integer
        this.serviceDate = castToLocalDate(row[2]);            // FECHA es LocalDate
        this.startHour = castToLocalTime(row[3]);              // FECHA_INICIO es LocalTime
        this.endHour = castToLocalTime(row[4]);                // FECHA_FIN es LocalTime
        this.totalServiceHours = castToDouble(row[5]);         // TOTAL_HORAS_SERVICIO es double
        this.comments = castToString(row[6]);                  // COMENTARIOS es String
        this.clientName = castToString(row[7]);                // NOMBRES es String
        this.clientSurname = castToString(row[8]);             // APELLIDOS es String
        this.addressService = castToString(row[9]);            // DIRECCION es String
        this.idService = castToLong(row[10]);                  // IDSerivicio es Integer
        this.serviceDescription = castToString(row[11]);       // DESCRIPCION es String
        this.employeeDocument = castToString(row[12]);         //NUMERO_DOCUMENTO es String
        this.employeeName = castToString(row[13]);             // NOMBRES es String
        this.employeeSurname = castToString(row[14]);          // APELLIDOS es String
    }

    // Método helper para casting seguro a Double
    private Double castToDouble(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Métodos helper para casting seguro
    private Long castToLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) {
            return (Long) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer castToInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String castToString(Object obj) {
        if (obj == null) return null;
        return obj.toString();
    }

    private LocalDate castToLocalDate(Object obj) {
        if (obj == null) return null;
        if (obj instanceof java.sql.Date) {
            return ((java.sql.Date) obj).toLocalDate();
        }
        if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        }
        try {
            return LocalDate.parse(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalTime castToLocalTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof java.sql.Time) {
            return ((java.sql.Time) obj).toLocalTime();
        }
        if (obj instanceof LocalTime) {
            return (LocalTime) obj;
        }
        try {
            return LocalTime.parse(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    // Métodos para combinaciones
    public String getNombreCompletoCliente() {
        if (clientName == null && clientSurname == null) return null;
        return (clientName != null ? clientName : "") + " " +
                (clientSurname != null ? clientSurname : "").trim();
    }
}

