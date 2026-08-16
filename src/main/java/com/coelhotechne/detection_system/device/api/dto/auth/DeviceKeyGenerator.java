package com.coelhotechne.detection_system.device.api.dto.auth;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class DeviceKeyGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static  final int KEY_BYTE_LENGTH = 32; //256 bits

    public String generate(){
        byte[] randomBytes = new byte[KEY_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
