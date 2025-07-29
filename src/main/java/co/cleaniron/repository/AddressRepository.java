package co.cleaniron.repository;

import co.cleaniron.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query(value = """ 
                   SELECT DISTINCT
                        ciudad 
                   FROM ubicaciones
                   WHERE ciudad IS NOT NULL 
                   ORDER BY ciudad ASC""", nativeQuery = true)
    List<String> findListCities();
}
