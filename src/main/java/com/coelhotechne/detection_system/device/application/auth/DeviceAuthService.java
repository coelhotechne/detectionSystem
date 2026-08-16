package com.coelhotechne.detection_system.device.application.auth;


import com.coelhotechne.detection_system.device.api.dto.auth.DeviceAuthResponse;

import java.util.UUID;

public interface DeviceAuthService {
    DeviceAuthResponse authenticate(UUID uuid, String providedAccessKey);
    void requireValidAccessKey(UUID uuid,String providedKey);
    String rotateAccessKey(UUID uuid);
}
