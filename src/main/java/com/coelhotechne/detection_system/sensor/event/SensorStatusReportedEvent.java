package com.coelhotechne.detection_system.sensor.event;

import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;

import java.time.Instant;
import java.util.UUID;

public record SensorStatusReportedEvent(
        UUID eventId,
        UUID sensorId,
        SensorStatus reportedStatus,
        Instant occurredAt
) implements SensorEvent {
}
