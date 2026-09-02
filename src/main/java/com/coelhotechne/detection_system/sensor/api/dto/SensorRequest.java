package com.coelhotechne.detection_system.sensor.api.dto;

import com.coelhotechne.detection_system.batterysupply.domain.PowerSupply;
import com.coelhotechne.detection_system.installation.domain.Installation;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;
import com.coelhotechne.detection_system.zone.domain.Zone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SensorRequest(
        @NotBlank
        String name,
        @NotNull
        LocalDateTime activationTime,
        @NotNull
        BigDecimal memoryUsed,
        @NotNull
        BigDecimal dataTransferValue,
        @NotBlank
        String dataDescription,
        @NotNull
        Installation installation,
        @NotNull
        UUID zoneUUID,
        @Valid
        PowerSupply powerSupply

) {
}