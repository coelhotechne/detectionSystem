package com.coelhotechne.detection_system.device.api.dto.auth;

import com.coelhotechne.detection_system.device.domain.enums.DeviceMessage;

public record DeviceAuthResponse(
        DeviceMessage result,
        String message
) {
}