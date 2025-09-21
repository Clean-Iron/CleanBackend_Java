package co.cleaniron.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@DynamicUpdate
@Entity
@Table(name = "Agenda")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Business_Key", nullable = false, length = 100)
    private String businessKey;

    @ManyToOne
    @JoinColumn(name = "Numero_Documento", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "Ubicacion_Id", nullable = false)
    private Address serviceAddress;

    @ManyToMany
    @JoinTable(
            name = "agenda_empleados",
            joinColumns = @JoinColumn(name = "Agenda_Id"),
            inverseJoinColumns = @JoinColumn(name = "Empleado_Id")
    )
    private Set<Employee> employees;

    @ManyToMany
    @JoinTable(
            name = "agenda_servicios",
            joinColumns = @JoinColumn(name = "Agenda_Id"),
            inverseJoinColumns = @JoinColumn(name = "Servicio_Id")
    )
    private Set<Service> services;

    @Column(name = "Fecha", nullable = false)
    private LocalDate date;

    @Column(name = "Hora_Inicio", nullable = false)
    private LocalTime startHour;

    @Column(name = "Hora_Fin", nullable = false)
    private LocalTime endHour;

    @Min(0) @Max(60)
    @Column(name = "Tiempo_Descanso", nullable = false)
    @ColumnDefault("0")
    private Integer breakMinutes = 0;

    /** Horas netas del servicio = (fin - inicio) - descanso (en horas). */
    @Column(name = "Total_Horas_Servicio")
    private Double totalServiceHours;

    @Column(name = "Comentarios")
    private String comments;

    @Column(name = "Estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceState state;

    @Column(name = "Tipo_Recurrencia", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    // === Formateadores para businessKey ===
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE; // YYYYMMDD
    private static final DateTimeFormatter HOUR_FMT = DateTimeFormatter.ofPattern("HHmm");

    // Setter defensivo por si viene null desde DTO/mapper
    public void setBreakMinutes(Integer minutes) {
        if (minutes == null) minutes = 0;
        if (minutes < 0) minutes = 0;
        if (minutes > 60) minutes = 60;
        this.breakMinutes = minutes;
    }

    @PrePersist
    @PreUpdate
    private void prepareEntity() {
        // Normaliza descanso
        if (breakMinutes == null) breakMinutes = 0;
        if (breakMinutes < 0) breakMinutes = 0;
        if (breakMinutes > 60) breakMinutes = 60;

        // 1) businessKey (recalcula con ciudad-fecha-horas-documento)
        if (serviceAddress != null && serviceAddress.getCity() != null
                && date != null && startHour != null && endHour != null
                && client != null && client.getDocument() != null) {

            String city = serviceAddress.getCity();
            String cityCode = city.replaceAll("\\s+", "")
                    .substring(0, Math.min(3, city.length()))
                    .toUpperCase();

            String dateKey = date.format(DATE_FMT);
            String hours = startHour.format(HOUR_FMT) + "-" + endHour.format(HOUR_FMT);
            String clientDoc = client.getDocument();

            this.businessKey = String.join("_", cityCode, dateKey, hours, clientDoc);
        }

        // 2) totalServiceHours = neto en horas
        if (startHour != null && endHour != null) {
            long grossMinutes = Duration.between(startHour, endHour).toMinutes();
            long netMinutes = Math.max(0, grossMinutes - breakMinutes);
            this.totalServiceHours = netMinutes / 60.0;
        }
    }
}
