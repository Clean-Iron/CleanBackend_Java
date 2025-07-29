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
            SELECT a.id,
                   a.numero_documento documentoCliente,
                   a.fecha FechaServicio,
                   a.hora_inicio,
                   a.hora_fin,
                   a.total_horas_servicio,
                   a.estado,
                   a.comentarios,
                   c.nombres NombreCliente,
                   c.apellidos ApellidoCliente,
                   u.ciudad Ciudad,
                   u.direccion DireccionServicio,
                   s.id IDServicio,
                   s.descripcion DescripcionServicio,
                   e.numero_documento documentoEmpleado,
                   e.nombres NombreEmpleado,
                   e.apellidos ApellidoEmpleado
            FROM agenda a
            LEFT JOIN clientes c ON c.numero_documento = a.numero_documento
            LEFT JOIN ubicaciones u ON a.ubicacion_id = u.ID
            LEFT JOIN agenda_servicios ss ON a.id = ss.agenda_id
            LEFT JOIN servicios s ON s.id = ss.servicio_id
            LEFT JOIN agenda_empleados se ON a.id = se.agenda_id
            LEFT JOIN empleados e ON se.empleado_id = e.numero_documento
            WHERE a.fecha = :dateService""", nativeQuery = true)
    List<Object[]> findScheduleDetailsByDate(@Param("dateService") LocalDate dateService);

    @Query(value = """
            SELECT a.id,
                   a.numero_documento documentoCliente,
                   a.fecha FechaServicio,
                   a.hora_inicio,
                   a.hora_fin,
                   a.estado,
                   a.comentarios,
                   c.nombres NombreCliente,
                   c.apellidos ApellidoCliente,
                   u.ciudad Ciudad,
                   u.direccion DireccionServicio,
                   s.id IDServicio,
                   s.descripcion DescripcionServicio,
                   e.numero_documento documentoEmpleado,
                   e.nombres NombreEmpleado,
                   e.apellidos ApellidoEmpleado
            FROM agenda a
            LEFT JOIN clientes c ON c.numero_documento = a.numero_documento
            LEFT JOIN ubicaciones u ON a.ubicacion_id = u.id
            LEFT JOIN agenda_servicios ss ON a.id = ss.agenda_id
            LEFT JOIN servicios s ON s.id = ss.servicio_id
            LEFT JOIN agenda_empleados se ON a.id = se.agenda_id
            LEFT JOIN empleados e ON se.empleado_id = e.numero_documento
            WHERE a.fecha = :dateService AND u.ciudad = :city
                AND (TRIM(LOWER(c.nombres)) LIKE LOWER(CONCAT('%', :name, '%'))
                OR TRIM(LOWER(c.apellidos)) LIKE LOWER(CONCAT('%', :surname, '%')));""", nativeQuery = true)
    List<Object[]> findScheduleDetailsByDateCityClient(
            @Param("dateService") LocalDate dateService,
            @Param("city") String city,
            @Param("name") String name,
            @Param("surname") String surname);

    @Query(value = """
            SELECT a.id,
                   a.numero_documento documentoCliente,
                   a.fecha FechaServicio,
                   a.hora_inicio,
                   a.hora_fin,
                   a.total_horas_servicio,
                   a.comentarios,
                   c.nombres NombreCliente,
                   c.apellidos ApellidoCliente,
                   u.direccion DireccionServicio,
                   s.id IDServicio,
                   s.descripcion DescripcionServicio,
                   e.numero_documento documentoEmpleado,
                   e.nombres NombreEmpleado,
                   e.apellidos ApellidoEmpleado
            FROM agenda a
            LEFT JOIN clientes c ON c.numero_documento = a.numero_documento
            LEFT JOIN ubicaciones u ON a.ubicacion_id = u.id
            LEFT JOIN agenda_servicios ss ON a.id = ss.agenda_id
            LEFT JOIN servicios s ON s.id = ss.servicio_id
            LEFT JOIN agenda_empleados se ON a.id = se.agenda_id
            LEFT JOIN empleados e ON se.empleado_id = e.numero_documento
        WHERE e.numero_documento = :doc
            AND EXTRACT(YEAR  FROM a.fecha)  = :year
            AND EXTRACT(MONTH FROM a.fecha)  = :month
        """, nativeQuery = true
    )
    List<Object[]> findServicesFromEmployeeByMonth(
            @Param("doc") String doc,
            @Param("year") String year,
            @Param("month") String month
    );
}