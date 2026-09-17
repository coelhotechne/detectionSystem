package com.coelhotechne.detection_system.sensor.application.event;

import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.domain.SensorDiagnosticsThresholds;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorNiche;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;
import com.coelhotechne.detection_system.sensor.event.*;
import com.coelhotechne.detection_system.sensor.exceptions.SensorConcurrentModificationException;
import com.coelhotechne.detection_system.sensor.exceptions.SensorNotFoundException;
import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Log4j2
public class SensorEventApplier {
    private final SensorRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final SensorDiagnosticsThresholds thresholds;
    private final Map<SensorNiche, SensorNicheReadingHandler> nicheHandlers;

    public SensorEventApplier(
            SensorRepository sensorRepository,
            ApplicationEventPublisher eventPublisher,
            SensorDiagnosticsThresholds thresholds,
            List<SensorNicheReadingHandler> handler
            ){
        this.repository=sensorRepository;
        this.eventPublisher=eventPublisher;
        this.thresholds=thresholds;
        this.nicheHandlers= new EnumMap<>(SensorNiche.class);
        handler.forEach(h -> this.nicheHandlers.put(h.niche(),h));
    }
    @Transactional
    public void apply(SensorEvent sensorEvent){
        if (sensorEvent instanceof SensorTelemetryEvent e) {
            applyTelemetry(e);
        } else if (sensorEvent instanceof SensorStatusReportedEvent e) {
            applyReportedStatus(e);
        } else if (sensorEvent instanceof SensorDetectionEvent e) {
            applyDetection(e);
        } else if (sensorEvent instanceof SensorStatusEvent e) {
            log.warn("SensorStatusEvent recebido como entrada — ele é evento de SAÍDA (auditoria), " +
                    "emitido por esta própria classe: {}", e);
        } else {
            throw new IllegalStateException("Unhandled SensorEvent type: " + sensorEvent.getClass());
        }
    }

    private void applyTelemetry(SensorTelemetryEvent event) {
        Sensor sensor = requireSensor(event.sensorId());

        Sensor.DiagnosticsOutcome outcome = sensor.applyDiagnostics(
                event.diagnostics(),
                event.occurredAt(),
                thresholds,
                thresholds.heartbeatWriteInterval());
        if (!outcome.persist()) {
            return;
        }

        Sensor saved = saveOrConflict(sensor);
        publishStatusChangeIfAny(saved.getUuid(), outcome.previousStatus(),
                saved.getSensorStatus(), event.occurredAt());
    }
    private void applyDetection(SensorDetectionEvent event) {
        Sensor sensor = requireSensor(event.sensorId());

        SensorNicheReadingHandler handle = nicheHandlers.get(sensor.getSensorNiche());
        if (handle != null) {
            handle.handler(sensor, event.detection());
        } else {
            log.debug("Sem handle para o nicho {} (sensor {}) — detecção registrada sem interpretação",
                    sensor.getSensorNiche(), sensor.getUuid());
        }
        eventPublisher.publishEvent(event);
    }

    private void applyReportedStatus(SensorStatusReportedEvent event) {
        Sensor sensor = requireSensor(event.sensorId());
        SensorStatus previous = sensor.getSensorStatus();

        boolean changed = sensor.applyReportedStatus(event.reportedStatus(), event.occurredAt());
        if (!changed) {
            return;
        }

        Sensor saved = saveOrConflict(sensor);
        publishStatusChangeIfAny(saved.getUuid(), previous, saved.getSensorStatus(), event.occurredAt());
    }

    private Sensor requireSensor(UUID uuid){
    return repository
            .findById(uuid)
            .orElseThrow(()-> new SensorNotFoundException(uuid.toString(),"Sensor not found!"));

    }

    private Sensor saveOrConflict(Sensor sensor){
        try {
            return repository.saveAndFlush(sensor);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new SensorConcurrentModificationException(sensor.getUuid().toString(), ex);
        }
    }

    private void publishStatusChangeIfAny(UUID sensorId, SensorStatus previus, SensorStatus current, Instant occurredAt){
    if (previus!=current){
        eventPublisher.publishEvent(new SensorStatusEvent(UUID.randomUUID(),sensorId,previus,current,occurredAt));
    }
    }
}
