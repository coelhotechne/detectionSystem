package com.coelhotechne.detection_system.sensor.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Duration;

@ConfigurationProperties(prefix = "sensor.diagnostics")
public record SensorDiagnosticsThresholds(
        BigDecimal dataTransferOutOfSpec,
        Duration heartbeatWriteInterval
) {
    public SensorDiagnosticsThresholds {
        if (dataTransferOutOfSpec == null) {
            dataTransferOutOfSpec = BigDecimal.valueOf(95);
        }
        if (heartbeatWriteInterval == null) {
            heartbeatWriteInterval = Duration.ofSeconds(60);
        }
    }
}