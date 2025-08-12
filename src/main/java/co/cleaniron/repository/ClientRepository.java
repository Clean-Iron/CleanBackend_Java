package co.cleaniron.repository;

import co.cleaniron.model.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    @EntityGraph(attributePaths = "addresses")
    Optional<Client> findById(String document);

    @EntityGraph(attributePaths = "addresses")
    @Query("select distinct c from Client c")
    List<Client> findAll();

    @EntityGraph(attributePaths = "addresses")
    Optional<Client> findByDocument(String document);

    @Query("""
              SELECT DISTINCT c
              FROM Client c
              LEFT JOIN FETCH c.addresses
              WHERE c.document = :doc
            """)
    Optional<Client> findByDocumentWithAddresses(@Param("doc") String doc);

    @Query("""
              select distinct c
              from Client c
              join fetch c.addresses a
              where a.city = :city
            """)
    List<Client> findAllClientsWithAddressInACity(@Param("city") String city);
}
