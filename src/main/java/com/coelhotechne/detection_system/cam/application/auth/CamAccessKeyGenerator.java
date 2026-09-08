package com.coelhotechne.detection_system.cam.application.auth;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CamAccessKeyGenerator {

    private static final int KEY_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        byte[] bytes = new byte[KEY_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
