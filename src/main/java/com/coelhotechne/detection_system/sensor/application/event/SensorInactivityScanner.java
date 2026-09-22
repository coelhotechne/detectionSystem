package com.coelhotechne.detection_system.sensor.application.event;

import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;
import com.coelhotechne.detection_system.sensor.event.SensorStatusEvent;
import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class SensorInactivityScanner {

    private final SensorRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final Duration window;

    public SensorInactivityScanner(SensorRepository repository, ApplicationEventPublisher eventPublisher,
                                   @Value("${sensor.inactivity.window:5m}")Duration window){
    this.repository=repository;
    this.eventPublisher=eventPublisher;
    this.window=window;
    }
    @Scheduled(fixedDelayString = "${sensor.inactivity.scan-interval-ms:30000}")
    @Transactional
    public void markStaleSensorDisconnected(){
        Instant threshold = Instant.now().minus(window);
        List<Sensor>stale =repository.findStaleSensors(threshold, SensorStatus.DISCONNECTED);
        for (Sensor sensor : stale){
            SensorStatus previus = sensor.getSensorStatus();
            if (!sensor.markDisconnected()){
                continue;
            }
            publish(sensor.getUuid(),previus);
        }
        if (!stale.isEmpty()) {
            log.info("Inactivity sweep: {} sensor(s) not reading since {}", stale.size(), threshold);
        }
    }
    private void publish(UUID sensorId, SensorStatus previous) {
        eventPublisher.publishEvent(new SensorStatusEvent(
                UUID.randomUUID(), sensorId, previous, SensorStatus.DISCONNECTED, Instant.now()));
    }

}
