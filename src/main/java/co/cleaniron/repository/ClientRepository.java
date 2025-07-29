package co.cleaniron.repository;

import co.cleaniron.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.addresses WHERE c.document = :doc")
    Optional<Client> findByDocumentWithAddresses(@Param("doc") String doc);

    @Query(value = """
            SELECT DISTINCT c.*
            FROM clientes c
            JOIN ubicaciones u ON c.numero_documento = u.numero_documento
            WHERE u.ciudad = :city;""", nativeQuery = true)
    List<Client> findAllClientsWithAddressInACity(@Param("city") String city);

    @Query(value = """
            SELECT DISTINCT u.ciudad
            FROM clientes c
            JOIN ubicaciones u
            ON c.numero_documento = u.numero_documento;""", nativeQuery = true)
    List<String> findAllCities();
}
