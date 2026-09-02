package com.coelhotechne.detection_system.sensor.domain.sensorevent;

import com.coelhotechne.detection_system.detection.domain.Detection;
import com.coelhotechne.detection_system.event.domain.Event;

import java.time.Instant;
import java.util.UUID;

public record SensorDetectionEvent (
        UUID eventId,
        UUID sensorId,
        UUID zoneUuid,
        Detection detection,
        Event event,
        Instant occurredAt
)implements SensorEvent{
}
