package com.coelhotechne.detection_system.sensor.domain.sensorevent;

import java.time.Instant;
import java.util.UUID;

public sealed interface SensorEvent
        permits SensorDetectionEvent,
        SensorStatusEvent,
        SensorTelemetryEvent {

    UUID eventId();
    UUID sensorId();
    Instant occurredAt();
}

