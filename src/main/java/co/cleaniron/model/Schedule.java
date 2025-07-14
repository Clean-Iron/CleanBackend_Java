package co.cleaniron.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

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

    // Métodos para cálculo automático
    @PrePersist
    @PreUpdate
    public void calculateServiceHours() {
        if (startHour != null && endHour != null) {
            Duration duration = Duration.between(startHour, endHour);
            this.totalServiceHours = duration.toMinutes() / 60.0;
        }
    }

    // Setters personalizados para recalcular automáticamente
    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
        calculateServiceHours();
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
        calculateServiceHours();
    }
}