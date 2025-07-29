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
    @Query(value = """
        SELECT e.*
        FROM empleados e
        WHERE e.ciudad = :city
        """, nativeQuery = true)
    List<Employee> findEmployeesByCity(
            @Param("city") String city
    );

    @Query(value = """
        SELECT e.*
        FROM empleados e
        WHERE e.ciudad = :city AND 
              e.numero_documento NOT IN (
            SELECT DISTINCT ae.empleado_id
            FROM agenda_empleados ae
            INNER JOIN agenda a ON ae.agenda_id = a.id
            WHERE a.fecha = :date
              AND (a.hora_inicio < :endHour AND a.hora_fin > :startHour)
              AND a.estado != 'CANCELADA'
        )
        ORDER BY e.nombres, e.apellidos
        """, nativeQuery = true)
    List<Employee> findAvailableEmployeesByDateStartHourEndHourCity(
            @Param("date") LocalDate date,
            @Param("startHour") LocalTime startHour,
            @Param("endHour") LocalTime endHour,
            @Param("city") String city
    );
}
