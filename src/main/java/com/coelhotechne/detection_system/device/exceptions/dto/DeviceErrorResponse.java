package com.coelhotechne.detection_system.device.exceptions.dto;

import java.time.LocalDateTime;

public record DeviceErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        String path,
        String deviceId,
        String deviceName,
        String deviceType
) {
}
