package com.coelhotechne.detection_system.sensor.domain.payload;

import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;

public record SensorStatusPayload(int statusCode) {
    public SensorStatus toSensorStatus() {
        return SensorStatus.fromCode(statusCode);
    }
}