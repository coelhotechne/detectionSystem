package com.coelhotechne.detection_system.sensor.event;

import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;

import java.time.Instant;
import java.util.UUID;

public record SensorStatusEvent (
        UUID eventId,
        UUID sensorId,
        SensorStatus previousStatus,
        SensorStatus newStatus,
        Instant occurredAt
)implements SensorEvent{
}
