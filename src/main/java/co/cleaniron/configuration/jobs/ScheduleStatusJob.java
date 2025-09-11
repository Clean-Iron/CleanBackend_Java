package co.cleaniron.configuration.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleStatusJob {

    private final ScheduleCompletionService service;

    @Value("${jobs.zone:America/Bogota}")
    private String zoneIdStr;

    /**
     * Marca COMPLETADO todo lo anterior a hoy (por defecto 00:05).
     * Configurable con: jobs.completePast.cron y jobs.zone
     */
    @Scheduled(cron = "${jobs.completePast.cron:0 5 0 * * *}", zone = "${jobs.zone:America/Bogota}")
    public void completePastNightly() {
        ZoneId zone = ZoneId.of(zoneIdStr);
        int updated = service.completePastWithRetry(zone);
        if (updated > 0) {
            log.info("Nightly job: {} servicios anteriores a hoy -> COMPLETADO", updated);
        }
    }
}

