package co.cleaniron.repository;

import co.cleaniron.model.Schedule;
import co.cleaniron.model.ServiceState;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query(value = "SELECT 1", nativeQuery = true)
    int ping();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update Schedule s
           set s.state = :completed
         where s.date < :today
           and s.state = :scheduled
    """)
    int bulkCompletePast(
            @Param("today") LocalDate today,
            @Param("scheduled") ServiceState scheduled,
            @Param("completed") ServiceState completed
    );

    @Query("""
                SELECT a.id,
                       c.document,
                       a.date,
                       a.startHour,
                       a.endHour,
                       a.totalServiceHours,
                       a.state,
                       a.comments,
                       a.recurrenceType,
                       c.name,
                       c.surname,
                       u.city,
                       u.address,
                       s.id,
                       s.description,
                       e.document,
                       e.name,
                       e.surname
                FROM Schedule a
                LEFT JOIN a.client c
                LEFT JOIN a.serviceAddress u
                LEFT JOIN a.services s
                LEFT JOIN a.employees e
                WHERE a.date = :dateService
            """)
    List<Object[]> findScheduleDetailsByDate(@Param("dateService") LocalDate dateService);

    @Query("""
                SELECT a.id,
                       a.client.document,
                       a.date,
                       a.startHour,
                       a.endHour,
                       a.totalServiceHours,
                       a.state,
                       a.comments,
                       a.recurrenceType,
                       c.name,
                       c.surname,
                       u.city,
                       u.address,
                       s.id,
                       s.description,
                       e.document,
                       e.name,
                       e.surname
                FROM Schedule a
                LEFT JOIN a.client c
                LEFT JOIN a.serviceAddress u
                LEFT JOIN a.services s
                LEFT JOIN a.employees e
                WHERE a.date = :dateService
                  AND (:city IS NULL OR u.city = :city)
                  AND (:name IS NULL OR LOWER(TRIM(c.name)) LIKE CONCAT('%', LOWER(:name), '%'))
                  AND (:surname IS NULL OR LOWER(TRIM(c.surname)) LIKE CONCAT('%', LOWER(:surname), '%'))
                ORDER BY a.date, a.id, e.surname, e.name
            """)
    List<Object[]> findScheduleDetailsByDateCityClient(
            @Param("dateService") LocalDate dateService,
            @Param("city") String city,
            @Param("name") String name,
            @Param("surname") String surname
    );

    @Query("""
            SELECT s.id,
                   c.document,
                   s.date,
                   s.startHour,
                   s.endHour,
                   s.totalServiceHours,
                   s.state,
                   s.comments,
                   s.recurrenceType,
                   c.name,
                   c.surname,
                   a.city,
                   a.address,
                   sv.id,
                   sv.description,
                   e.document,
                   e.name,
                   e.surname
            FROM Schedule s
              JOIN s.client c
              JOIN s.serviceAddress a
              LEFT JOIN s.services sv
              LEFT JOIN s.employees e
            WHERE s.date >= :fromDate
              AND s.date  < :toDate
              AND EXISTS (
                  SELECT 1 FROM s.employees se
                  WHERE se.document = :doc
              )
            ORDER BY s.date, s.id, e.surname, e.name
            """)
    List<Object[]> findServicesByEmployeeAndDateRange(
            @Param("doc") String doc,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT s.id,
                   c.document,
                   s.date,
                   s.startHour,
                   s.endHour,
                   s.totalServiceHours,
                   s.state,
                   s.comments,
                   s.recurrenceType,
                   c.name,
                   c.surname,
                   a.city,
                   a.address,
                   sv.id,
                   sv.description,
                   e.document,
                   e.name,
                   e.surname
            FROM Schedule s
              JOIN s.client c
              JOIN s.serviceAddress a
              LEFT JOIN s.services sv
              LEFT JOIN s.employees e
            WHERE s.date >= :fromDate
              AND s.date  < :toDate
              AND TRIM(c.document) = TRIM(:clientDoc)
            ORDER BY s.date, s.id, e.surname, e.name
            """)
    List<Object[]> findServicesByClientAndDateRange(
            @Param("clientDoc") String clientDoc,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
                SELECT s.id,
                       c.document,
                       s.date,
                       s.startHour,
                       s.endHour,
                       s.totalServiceHours,
                       s.state,
                       s.comments,
                       s.recurrenceType,
                       c.name,
                       c.surname,
                       a.city,
                       a.address,
                       sv.id,
                       sv.description,
                       e.document,
                       e.name,
                       e.surname
                FROM Schedule s
                  JOIN s.client c
                  JOIN s.serviceAddress a
                  LEFT JOIN s.services sv
                  LEFT JOIN s.employees e
                WHERE s.date >= :fromDate
                  AND s.date  < :toDate
                  AND ( :employeeCity IS NULL OR :employeeCity = ''
                        OR EXISTS (
                            SELECT 1
                            FROM Schedule s2
                              JOIN s2.employees e2
                            WHERE s2.id = s.id
                              AND UPPER(TRIM(e2.city)) = UPPER(TRIM(:employeeCity))
                        )
                      )
                ORDER BY s.date, s.id, e.surname, e.name
            """)
    List<Object[]> findServicesByEmployeeCityByMonth(
            @Param("employeeCity") String employeeCity,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    default List<Object[]> findServicesFromEmployeeByMonth(
            String doc, String year, String month
    ) {
        int y = Integer.parseInt(year);
        int m = Integer.parseInt(month);
        LocalDate from = LocalDate.of(y, m, 1);
        LocalDate to = from.plusMonths(1);
        return findServicesByEmployeeAndDateRange(doc, from, to);
    }

    default List<Object[]> findServicesFromClientByMonth(
            String clientDoc, String year, String month
    ) {
        int y = Integer.parseInt(year);
        int m = Integer.parseInt(month);
        LocalDate from = LocalDate.of(y, m, 1);
        LocalDate to = from.plusMonths(1);
        return findServicesByClientAndDateRange(clientDoc, from, to);
    }

    default List<Object[]> findServicesByCityAndMonth(
            String city, String year, String month
    ) {
        int y = Integer.parseInt(year);
        int m = Integer.parseInt(month);
        LocalDate from = LocalDate.of(y, m, 1);
        LocalDate to = from.plusMonths(1);
        String normalizedCity = (city != null && !city.trim().isEmpty()) ? city : null;
        return findServicesByEmployeeCityByMonth(normalizedCity, from, to);
    }
}