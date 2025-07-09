package co.cleaniron.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "Ubicaciones")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Direccion", nullable = false)
    private String address;

    @Column(name = "Ciudad", nullable = false)
    private String city;

    @Column(name = "Descripcion")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NumeroDocumento", nullable = false)
    @JsonBackReference
    private Client client;

    public Address(String address, String city, String description) {
        this.address = address;
        this.city = city;
        this.description = description;
    }
}

