package co.cleaniron.configuration.jobs;

import co.cleaniron.model.ServiceState;
import co.cleaniron.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLTransientConnectionException;
import java.sql.SQLTransientException;
import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleCompletionService {

    private final ScheduleRepository repo;

    /**
     * Completa todos los servicios anteriores a 'today' que sigan en PROGRAMADO.
     * 1) ping -> reanuda BD serverless si está en pausa
     * 2) bulk update
     */
    @Retryable(
            retryFor = {
                    TransientDataAccessException.class,
                    CannotGetJdbcConnectionException.class,
                    JDBCConnectionException.class,
                    SQLTransientConnectionException.class,
                    SQLTransientException.class
            },
            maxAttempts = 8,
            backoff = @Backoff(delay = 2000, multiplier = 1.5)
    )
    @Transactional
    public int completePastWithRetry(ZoneId zone) {
        repo.ping();
        LocalDate today = LocalDate.now(zone);

        int updated = repo.bulkCompletePast(
                today,
                ServiceState.PROGRAMADA,
                ServiceState.COMPLETADA
        );
        log.info("bulkCompletePast (fecha<{}): {} filas", today, updated);
        return updated;
    }
}

