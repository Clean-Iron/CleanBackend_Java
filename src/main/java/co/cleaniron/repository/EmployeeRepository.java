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
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query(value = """
        SELECT e.*
        FROM EMPLEADOS e
        WHERE e.CIUDAD = :city AND 
              e.NUMERO_DOCUMENTO NOT IN (
            SELECT DISTINCT ae.EMPLEADO_ID
            FROM AGENDA_EMPLEADOS ae
            INNER JOIN AGENDA a ON ae.AGENDA_ID = a.ID
            WHERE a.FECHA = :date
              AND (a.HORA_INICIO < :endHour AND a.HORA_FIN > :startHour)
              AND a.ESTADO != 'CANCELADA'
        )
        ORDER BY e.Nombres, e.Apellidos
        """, nativeQuery = true)
    List<Employee> findAvailableEmployeesByDateStartHourEndHourCity(
            @Param("date") LocalDate date,
            @Param("startHour") LocalTime startHour,
            @Param("endHour") LocalTime endHour,
            @Param("city") String city
    );
}
