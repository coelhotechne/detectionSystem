package com.coelhotechne.detection_system.sensor.event;

import com.coelhotechne.detection_system.batterysupply.domain.PowerSupplyStatus;

import java.time.Instant;
import java.util.UUID;

public record SensorPowerSupplyEvent(
        UUID eventId,
        UUID sensorId,
        PowerSupplyStatus previousStatus,
        PowerSupplyStatus newStatus,
        Instant occurredAt
) implements SensorEvent{
}
