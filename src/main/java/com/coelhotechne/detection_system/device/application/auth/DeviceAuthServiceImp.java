package com.coelhotechne.detection_system.device.application.auth;

import com.coelhotechne.detection_system.device.api.dto.auth.DeviceAuthResponse;
import com.coelhotechne.detection_system.device.api.dto.auth.DeviceKeyGenerator;
import com.coelhotechne.detection_system.device.domain.Device;
import com.coelhotechne.detection_system.device.domain.enums.DeviceMessage;
import com.coelhotechne.detection_system.device.exceptions.DeviceAuthenticationException;
import com.coelhotechne.detection_system.device.exceptions.DeviceNotFoundException;
import com.coelhotechne.detection_system.device.infrastructure.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class DeviceAuthServiceImp implements DeviceAuthService{
    private final DeviceKeyGenerator keyGenerator;
    private final DeviceRepository repository;
    @Override
    public DeviceAuthResponse authenticate(UUID uuid, String providedAccessKey) {
        Device entity = repository.findById(uuid)
                .orElseThrow(()-> new DeviceNotFoundException(uuid.toString(),null,null,"Device id not found!"));
        if(!entity.getAccessKey().equals(providedAccessKey)){
            log.warn("Authentication failed for device {}",uuid);
            return new DeviceAuthResponse(DeviceMessage.DENIED,"Invalid Access Key");
        }
        entity.setLastCommunication(LocalDateTime.now());
        repository.save(entity);
        return new DeviceAuthResponse(DeviceMessage.AUTHORIZED,"Authenticated successfully!");
    }

    @Override
    public void requireValidAccessKey(UUID uuid, String providedKey) {
        Device entity = repository.findById(uuid)
                .orElseThrow(()-> new DeviceNotFoundException(uuid.toString(),null,null,"Device id not found!"));
        if (!entity.getAccessKey().equals(providedKey)){
            log.warn("Access Key rejected for device{}",uuid);
            throw new DeviceAuthenticationException(uuid.toString(),DeviceMessage.DENIED,"Access key invalid!");
        }
    }

    @Override
    public String rotateAccessKey(UUID uuid) {
        Device entity = repository
                .findById(uuid)
                .orElseThrow(()-> new DeviceNotFoundException(uuid.toString(), null, null, "Device id not found!"));
        String newKey = keyGenerator.generate();
        entity.setAccessKey(newKey);
        repository.save(entity);
        return newKey;
    }
}