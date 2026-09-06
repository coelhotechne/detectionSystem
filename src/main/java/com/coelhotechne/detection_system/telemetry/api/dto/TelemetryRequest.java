package com.coelhotechne.detection_system.telemetry.api.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

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
        Instant measuredAt

) {
}