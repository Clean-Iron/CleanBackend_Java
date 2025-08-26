package co.cleaniron.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Clientes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Client {
    @Id
    @Column(name = "NumeroDocumento", nullable = false)
    private String document;

    @Column(name = "TipoDocumento", nullable = false)
    private String typeId;

    @Column(name = "Nombres")
    private String name;

    @Column(name = "Apellidos")
    private String surname;

    @Column(name = "NumeroContacto")
    private String phone;

    @Column(name = "CorreoElectronico")
    private String email;

    @Column(name = "Comentarios")
    private String comments;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<Address> addresses = new HashSet<>();

    public Client(String document, String typeId, String name, String surname, String phone, String email, String comments) {
        this.document = document;
        this.typeId = typeId;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.comments = comments;
        this.addresses = new HashSet<>();
    }
}

