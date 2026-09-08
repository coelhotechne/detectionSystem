package com.coelhotechne.detection_system.cam.infrastructure;

import com.coelhotechne.detection_system.cam.api.dto.RtspCredentials;

public interface CamCredentialResolver {
    RtspCredentials resolve(String credentialRef);
}

