package co.cleaniron.repository;

import co.cleaniron.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @Query("""
            SELECT e
            FROM Employee e
            WHERE e.city = :city
            """)
    List<Employee> findEmployeesByCity(@Param("city") String city);

    @Query("""
            SELECT e
            FROM Employee e
            WHERE e.city = :city
              AND NOT EXISTS (
                SELECT 1
                FROM Schedule s
                JOIN s.employees se
                WHERE se = e
                  AND s.date = :date
                  AND s.startHour < cast(:endHour as time)
                  AND s.endHour   > cast(:startHour as time)
              )
            ORDER BY e.name, e.surname
            """)
    List<Employee> findAvailableEmployeesByDateStartHourEndHourCity(
            @Param("date") LocalDate date,
            @Param("startHour") LocalTime startHour,
            @Param("endHour") LocalTime endHour,
            @Param("city") String city
    );
}
