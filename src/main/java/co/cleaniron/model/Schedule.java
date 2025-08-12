package co.cleaniron.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    // Opción 1: Campo calculado que se guarda en la base de datos
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

    // Formateadores
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE; // YYYYMMDD
    private static final DateTimeFormatter HOUR_FMT = DateTimeFormatter.ofPattern("HHmm");

    /**
     * Un único callback para PrePersist y PreUpdate:
     * - Genera businessKey si es nueva.
     * - Calcula totalServiceHours siempre.
     */
    @PrePersist
    @PreUpdate
    private void prepareEntity() {
        // 1️⃣ businessKey (solo si aún no existe)
        String cityCode = serviceAddress.getCity()
                .replaceAll("\\s+", "")
                .substring(0, Math.min(3, serviceAddress.getCity().length()))
                .toUpperCase();
        String dateKey = date.format(DATE_FMT);
        String hours = startHour.format(HOUR_FMT) + "-" + endHour.format(HOUR_FMT);
        String client = getClient().getDocument();

        this.businessKey = String.join("_", cityCode, dateKey, hours, client);

        // 2️⃣ totalServiceHours
        if (startHour != null && endHour != null) {
            Duration dur = Duration.between(startHour, endHour);
            this.totalServiceHours = dur.toMinutes() / 60.0;
        }
    }
}