package com.coelhotechne.detection_system.cam.infrastructure.protocol;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Profile("dev")
public class DevCamCredentialResolver implements CamCredentialResolver {
    @Override
    public RtspCredentials resolve(String credentialRef) {
        return null;
    }
}
