package com.coelhotechne.detection_system.sensor.domain.payload;

import java.math.BigDecimal;

public record SensorTelemetryPayload (
        Boolean status,
        BigDecimal memoryUsed,
        BigDecimal dataTransferValue,
        String dataDescription
){
}
