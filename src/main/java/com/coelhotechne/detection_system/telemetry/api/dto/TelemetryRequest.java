package com.coelhotechne.detection_system.telemetry.api.dto;


import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.telemetry.domain.Telemetry;
import com.coelhotechne.detection_system.zone.domain.Zone;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TelemetryRequest(

        @NotNull
        UUID zoneId,
        @NotNull
        UUID sensorId,
        @NotNull
        Integer measuredValue,
        @NotNull
        Instant measuredAt

) {
    public static Telemetry toEntity(TelemetryRequest telemetryRequest, Zone zone, Sensor sensor) {
        return new Telemetry(
                zone,
                sensor,
                telemetryRequest.measuredValue(),
                telemetryRequest.measuredAt()
        );
    }
}