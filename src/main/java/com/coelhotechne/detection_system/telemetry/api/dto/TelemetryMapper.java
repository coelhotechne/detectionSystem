package com.coelhotechne.detection_system.telemetry.api.dto;

import com.coelhotechne.detection_system.globalClass.mapper.GenericMapper;
import com.coelhotechne.detection_system.telemetry.domain.Telemetry;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class TelemetryMapper implements GenericMapper<Telemetry,TelemetryResponse,TelemetryRequest> {
    @Override
    public Telemetry toEntity(TelemetryRequest request) {
        Objects.requireNonNull(request,"Telemetry request cannot be null");
        Telemetry entity = new Telemetry();
        entity.setMeasuredValue(request.measuredValue());
        entity.setMeasuredAt(request.measuredAt());
        return entity;
    }

    @Override
    public TelemetryResponse toResponse(Telemetry entity) {
        Objects.requireNonNull(entity,"Telemetry cannot be null");
        UUID zoneid= entity.getZone() != null ? entity.getZone().getUuid() : null;
        UUID sensorid= entity.getSensor() != null ? entity.getSensor().getUuid() : null;

        return new TelemetryResponse(
                entity.getUuid(),
                entity.getVersion(),
                entity.getCreatedBy(),
                entity.getLastModifiedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                zoneid,
                sensorid,
                entity.getMeasuredValue(),
                entity.getMeasuredAt()
        );
    }
}
