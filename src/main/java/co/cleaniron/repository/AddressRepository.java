package co.cleaniron.repository;

import co.cleaniron.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query(value = """ 
                   SELECT DISTINCT 
                        CIUDAD 
                   FROM UBICACIONES 
                   WHERE CIUDAD IS NOT NULL 
                   ORDER BY CIUDAD ASC""", nativeQuery = true)
    List<String> findListCities();
}
