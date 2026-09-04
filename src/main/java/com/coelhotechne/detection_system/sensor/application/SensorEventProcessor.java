package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;
import com.coelhotechne.detection_system.sensor.event.*;
import com.coelhotechne.detection_system.sensor.exceptions.SensorConcurrentModificationException;
import com.coelhotechne.detection_system.sensor.exceptions.SensorNotFoundException;
import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class SensorEventProcessor {

    private final SensorRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void process(SensorEvent event) {
        if (event instanceof SensorTelemetryEvent e) {
            applyTelemetry(e);
        } else if (event instanceof SensorStatusReportedEvent e) {
            applyReportedStatus(e);
        } else if (event instanceof SensorDetectionEvent e) {
            applyDetection(e);
        } else if (event instanceof SensorStatusEvent e) {
            log.warn("SensorStatusEvent received in process() — it is an OUTPUT (audit) event that the" +
                    "processor itself emits; it should not be coming in as input: {}", e);
        } else {
            throw new IllegalStateException("Unhandled SensorEvent type: " + event.getClass());
        }
    }

    private void applyTelemetry(SensorTelemetryEvent event) {
        Sensor sensor = requireSensor(event.sensorId());
        SensorStatus previousStatus = sensor.applyDiagnostics(event.diagnostics(), event.occurredAt());
        Sensor saved = saveOrConflict(sensor);
        publishStatusChangeIfAny(saved.getUuid(), previousStatus, saved.getStatus(), event.occurredAt());
    }

    private void applyReportedStatus(SensorStatusReportedEvent event) {
        Sensor sensor = requireSensor(event.sensorId());
        SensorStatus previousStatus = sensor.getStatus();
        if (previousStatus == event.reportedStatus()) {
            return;
        }
        sensor.setStatus(event.reportedStatus());
        Sensor saved = saveOrConflict(sensor);
        publishStatusChangeIfAny(saved.getUuid(), previousStatus, saved.getStatus(), event.occurredAt());
    }

    private void applyDetection(SensorDetectionEvent event) {
        // resolução em Detection/Event reais depende das subclasses de Detection
        // ainda não recebidas (pendência #1 do doc de domínio) — TODO explícito
        throw new UnsupportedOperationException(
                "Resolution of SensorDetectionEvent within Detection/Event still depends on subclasses of " +
                        "Detection (pending item #1 from the domain document) — sensorId=" + event.sensorId());
    }

    private Sensor requireSensor(UUID sensorId) {
        return repository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(sensorId.toString(), "Sensor not found!"));
    }

    private Sensor saveOrConflict(Sensor sensor) {
        try {
            return repository.saveAndFlush(sensor);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new SensorConcurrentModificationException(sensor.getUuid().toString(), ex);
        }
    }

    private void publishStatusChangeIfAny(UUID sensorId, SensorStatus previous, SensorStatus current, Instant occurredAt) {
        if (previous != current) {
            eventPublisher.publishEvent(new SensorStatusEvent(UUID.randomUUID(), sensorId, previous, current, occurredAt));
        }
    }
}