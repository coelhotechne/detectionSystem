package com.coelhotechne.detection_system.device.domain;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeviceKeyGenerator {
    public String generate(){
        return UUID.randomUUID().toString();
    }
}
