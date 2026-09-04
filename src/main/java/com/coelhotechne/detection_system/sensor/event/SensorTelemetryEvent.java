package com.coelhotechne.detection_system.sensor.event;

import com.coelhotechne.detection_system.sensor.domain.payload.SensorTelemetryPayload;

import java.time.Instant;
import java.util.UUID;

public record SensorTelemetryEvent(
        UUID eventId,
        UUID sensorId,
        SensorTelemetryPayload diagnostics,
        Instant occurredAt
) implements SensorEvent{
}
