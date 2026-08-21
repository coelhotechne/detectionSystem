package com.coelhotechne.detection_system.sensor.api.dto;


import com.coelhotechne.detection_system.globalClass.mapper.GenericMapper;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import org.springframework.stereotype.Component;

import java.util.Objects;
@Component
public class SensorMapper implements GenericMapper<Sensor, SensorResponse, SensorRequest> {

    @Override
    public Sensor toEntity(SensorRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");

        Sensor entity = new Sensor();

        entity.setName(request.name());
        entity.setStatus(request.status());
        entity.setActivationTime(request.activationTime());
        entity.setMemoryUsed(request.memoryUsed());
        entity.setDataTransferValue(request.dataTransferValue());
        entity.setDataDescription(request.dataDescription());
        entity.setInstallationDate(request.installationDate());
        return entity;
    }

    @Override
    public SensorResponse toResponse(Sensor entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");

        return new SensorResponse(
                entity.getUuid(),
                entity.getName(),
                entity.isStatus(),
                entity.getActivationTime(),
                entity.getMemoryUsed(),
                entity.getDataTransferValue(),
                entity.getDataDescription(),
                entity.getInstallationDate(),
                entity.getZone().getUuid(),
                entity.getVersion(),
                entity.getCreatedBy(),
                entity.getLastModifiedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}