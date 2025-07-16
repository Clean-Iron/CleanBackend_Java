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
            FROM CLIENTES c
            JOIN UBICACIONES u ON c.NUMERO_DOCUMENTO = u.NUMERO_DOCUMENTO
            WHERE u.Ciudad = :city;""", nativeQuery = true)
    List<Client> findAllClientsWithAddressInACity(@Param("city") String city);

    @Query(value = """
            SELECT DISTINCT u.CIUDAD
            FROM CLIENTES c
            JOIN UBICACIONES u\s
            ON c.NUMERO_DOCUMENTO = u.NUMERO_DOCUMENTO;""", nativeQuery = true)
    List<String> findAllCities();
}
