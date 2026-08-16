package com.coelhotechne.detection_system.device.api.dto;

import com.coelhotechne.detection_system.device.domain.Device;
import jakarta.validation.constraints.NotBlank;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DeviceRequest(
        @NotBlank
        String deviceName,
        @NotBlank
        String deviceType,
        @NotBlank
        String ipAddress,
        Boolean status,
        LocalDateTime lastCommunication,
        @NotBlank
        String accessKey
) {
    public static Device toEntity(DeviceRequest deviceRequest){

        return new Device(
                deviceRequest.deviceName(),
                deviceRequest.deviceType(),
                deviceRequest.ipAddress(),
                deviceRequest.status(),
                deviceRequest.lastCommunication(),
                deviceRequest.accessKey()
        );
    }
}
