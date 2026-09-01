package com.coelhotechne.detection_system.sensor.api.dto;

import java.math.BigDecimal;

public record SensorTelemetryPayload (
        Boolean status,
        BigDecimal memoryUsed,
        BigDecimal dataTransferValue,
        String dataDescription
){
}
