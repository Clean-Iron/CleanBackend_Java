package co.cleaniron.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "Empleados")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Employee {
    @Id
    @Column(name = "NumeroDocumento", nullable = false)
    private String document;
    @Column(name = "Nombres", nullable = false)
    private String name;
    @Column(name = "Apellidos")
    private String surname;
    @Column(name = "NumeroContacto")
    private String phone;
    @Column(name = "CorreoElectronico")
    private String email;
    @Column(name = "DireccionResdidencia")
    private String addressResidence;
    @Column(name = "Ciudad")
    private String city;
    @Column(name = "FechaIngreso")
    private LocalDate entryDate;
    @Column(name = "Cargo")
    private String position;
    @Column(name = "Estado")
    private boolean state;
}