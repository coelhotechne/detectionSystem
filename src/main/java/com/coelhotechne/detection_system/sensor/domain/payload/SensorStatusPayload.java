package com.coelhotechne.detection_system.sensor.domain.payload;

import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorStatusPayload(
        @JsonProperty("status_code")
        Integer statusCode
) {
    public SensorStatus toSensorStatus() {
        if (statusCode == null) {
            throw new IllegalArgumentException("Payload de status sem 'status_code'");
        }
        return SensorStatus.fromCode(statusCode);
    }
}