package co.cleaniron.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Servicios")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="Descripcion", nullable = false)
    private String description;
    @Column(name="Duracion", nullable = false)
    private int hours;
    @Column(name="Precio", nullable = false)
    private double cost;
}

