package com.coelhotechne.detection_system.batterysupply.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Duration;

@ConfigurationProperties(prefix = "sensor.power")
public record PowerSupplyThresholds(
        BigDecimal lowThreshold,
        BigDecimal criticalThreshold,
        Duration heartbeatWriteInterval
) {
    public PowerSupplyThresholds {
        lowThreshold = lowThreshold != null ? lowThreshold : new BigDecimal("30");
        criticalThreshold = criticalThreshold != null ? criticalThreshold : new BigDecimal("15");
        heartbeatWriteInterval = heartbeatWriteInterval != null
                ? heartbeatWriteInterval : Duration.ofMinutes(15);
    }
}