package co.cleaniron.repository;

import co.cleaniron.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query(value = """
            SELECT a.ID,
                   a.NUMERO_DOCUMENTO documentoCliente,
                   a.FECHA FechaServicio,
                   a.HORA_INICIO,
                   a.HORA_FIN,
                   a.TOTAL_HORAS_SERVICIO,
                   a.ESTADO,
                   a.COMENTARIOS,
                   c.NOMBRES NombreCliente,
                   c.APELLIDOS ApellidoCliente,
                   u.CIUDAD Ciudad,
                   u.DIRECCION DireccionServicio,
                   s.DESCRIPCION DescripcionServicio,
                   e.NOMBRES NombreEmpleado,
                   e.APELLIDOS ApellidoEmpleado
            FROM AGENDA a
            LEFT JOIN CLIENTES c ON c.NUMERO_DOCUMENTO = a.NUMERO_DOCUMENTO
            LEFT JOIN UBICACIONES u ON a.UBICACION_ID = u.ID
            LEFT JOIN AGENDA_SERVICIOS ss ON a.ID = ss.AGENDA_ID
            LEFT JOIN SERVICIOS s ON s.ID = ss.SERVICIO_ID
            LEFT JOIN AGENDA_EMPLEADOS se ON a.ID = se.AGENDA_ID
            LEFT JOIN EMPLEADOS e ON se.EMPLEADO_ID = e.NUMERO_DOCUMENTO
            WHERE a.FECHA = :dateService""", nativeQuery = true)
    List<Object[]> findScheduleDetailsByDate(@Param("dateService") LocalDate dateService);

    @Query(value = """
            SELECT a.ID,
                   a.NUMERO_DOCUMENTO documentoCliente,
                   a.FECHA FechaServicio,
                   a.HORA_INICIO,
                   a.HORA_FIN,
                   a.ESTADO,
                   a.COMENTARIOS,
                   c.NOMBRES NombreCliente,
                   c.APELLIDOS ApellidoCliente,
                   u.CIUDAD Ciudad,
                   u.DIRECCION DireccionServicio,
                   s.DESCRIPCION DescripcionServicio,
                   e.NOMBRES NombreEmpleado,
                   e.APELLIDOS ApellidoEmpleado
            FROM AGENDA a
            LEFT JOIN CLIENTES c ON c.NUMERO_DOCUMENTO = a.NUMERO_DOCUMENTO
            LEFT JOIN UBICACIONES u ON a.UBICACION_ID = u.ID
            LEFT JOIN AGENDA_SERVICIOS ss ON a.ID = ss.AGENDA_ID
            LEFT JOIN SERVICIOS s ON s.ID = ss.SERVICIO_ID
            LEFT JOIN AGENDA_EMPLEADOS se ON a.ID = se.AGENDA_ID
            LEFT JOIN EMPLEADOS e ON se.EMPLEADO_ID = e.NUMERO_DOCUMENTO
            WHERE a.FECHA = :dateService AND u.CIUDAD = :city
                AND (TRIM(LOWER(c.NOMBRES)) LIKE LOWER(CONCAT('%', :name, '%'))
                OR TRIM(LOWER(c.APELLIDOS)) LIKE LOWER(CONCAT('%', :surname, '%')));""", nativeQuery = true)
    List<Object[]> findScheduleDetailsByDateCityClient(
            @Param("dateService") LocalDate dateService,
            @Param("city") String city,
            @Param("name") String name,
            @Param("surname") String surname);
}