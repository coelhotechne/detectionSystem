package com.coelhotechne.detection_system.sensor.domain.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorDetectionPayload(
        @JsonProperty("category")
        String category,
        @JsonProperty("description")
        String description,
        @JsonProperty("detected_at")
        Instant detectedAt
) {
}
