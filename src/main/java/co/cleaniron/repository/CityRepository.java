package co.cleaniron.repository;

import co.cleaniron.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByName(String name);

    @Query("""
            select distinct trim(c.name)
            from City c
            where c.name is not null and trim(c.name) <> ''
            """)
    List<String> findDistinctNames();
}
