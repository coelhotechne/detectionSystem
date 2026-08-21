package com.coelhotechne.detection_system.device.api.dto;

import com.coelhotechne.detection_system.globalClass.mapper.GenericMapper;
import com.coelhotechne.detection_system.device.domain.Device;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DeviceMapper implements GenericMapper<Device,DeviceResponse,DeviceRequest> {
    @Override
    public Device toEntity(DeviceRequest request) {
        Objects.requireNonNull(request,"Request cannot be null");
        Device entity = new Device();
        entity.setDeviceName(request.deviceName());
        entity.setDeviceType(request.deviceType());
        entity.setIpAdress(request.ipAddress());
        entity.setStatus(request.status());
        entity.setLastCommunication(request.lastCommunication());
        entity.setAccessKey(request.accessKey());
        return entity;
    }

    @Override
    public DeviceResponse toResponse(Device entity) {
        return new DeviceResponse(
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getLastModifiedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeviceName(),
                entity.getDeviceType(),
                entity.getIpAdress(),
                entity.getStatus(),
                entity.getLastCommunication()
        );
    }
}
