package com.coelhotechne.detection_system.sensor.event;

import java.time.Instant;

public record SensorDetectionPayload(
        String category,
        String description,
        Instant detectedAt
) {
}
