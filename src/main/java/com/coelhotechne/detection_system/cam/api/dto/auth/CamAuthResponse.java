package com.coelhotechne.detection_system.cam.api.dto.auth;

import com.coelhotechne.detection_system.cam.application.auth.enums.CamAuthMessage;

import java.time.Instant;
import java.util.UUID;

public record CamAuthResponse(
        UUID camId,
        CamAuthMessage message,
        Instant authenticatedAt
) {
}