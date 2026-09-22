package com.coelhotechne.detection_system.sensor.application.event;

import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;
import com.coelhotechne.detection_system.sensor.event.*;
import com.coelhotechne.detection_system.sensor.exceptions.SensorConcurrentModificationException;
import com.coelhotechne.detection_system.sensor.exceptions.SensorNotFoundException;
import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Log4j2
public class SensorEventProcessor {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BASE_BACKOFF_MILLIS = 20;

    private final SensorEventApplier applier;

    public void process(SensorEvent event) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                applier.apply(event);
                return;
            } catch (OptimisticLockingFailureException | SensorConcurrentModificationException e) {
                if (attempt == MAX_ATTEMPTS) {
                    log.error("Conflito otimista persistente no sensor {} após {} tentativas — evento descartado",
                            event.sensorId(), MAX_ATTEMPTS, e);
                    throw e;
                }
                // Jitter para não sincronizar as threads de ingestão numa
                // retentativa simultânea sobre a mesma linha quente.
                backoff(attempt);
                log.debug("Conflito otimista no sensor {} (tentativa {}/{}), repetindo",
                        event.sensorId(), attempt, MAX_ATTEMPTS);
            }
        }
    }

    private void backoff(int attempt) {
        try {
            long jitter = ThreadLocalRandom.current().nextLong(BASE_BACKOFF_MILLIS);
            Thread.sleep(BASE_BACKOFF_MILLIS * attempt + jitter);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

}