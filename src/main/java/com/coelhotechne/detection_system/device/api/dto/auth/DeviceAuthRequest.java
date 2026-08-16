package com.coelhotechne.detection_system.device.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DeviceAuthRequest(
        @NotBlank
        String accessKey
) {
}
