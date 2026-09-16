package com.coelhotechne.detection_system.sensor.api.dto;

import com.coelhotechne.detection_system.batterysupply.domain.PowerSupply;
import com.coelhotechne.detection_system.installation.domain.Installation;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorNiche;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SensorRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9_-]{1,15}$",
                message = "name must consist of up to 15 alphanumeric characters, '-', or '_' \n"
                        + "(used as an MQTT topic segment)")
        String name,
        @NotBlank
        SensorNiche sensorNiche,
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