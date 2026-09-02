package com.coelhotechne.detection_system.sensor.api.dto;


import com.coelhotechne.detection_system.globalClass.mapper.GenericMapper;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.exceptions.SensorWithoutZoneException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class SensorMapper implements GenericMapper<Sensor, SensorResponse, SensorRequest> {

    @Override
    public Sensor toEntity(SensorRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");

        Sensor entity = new Sensor();

        entity.setName(request.name());
        entity.setActivationTime(request.activationTime());
        entity.setMemoryUsed(request.memoryUsed());
        entity.setDataTransferValue(request.dataTransferValue());
        entity.setDataDescription(request.dataDescription());
        entity.setPowerSupply(request.powerSupply());
        entity.setInstallation(request.installation());
        return entity;
    }

    @Override
    public SensorResponse toResponse(Sensor entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        UUID zoneUuid = entity.getZone() != null ? entity.getZone().getUuid() : null;
        if (entity.getZone() == null){
            throw new SensorWithoutZoneException(entity.getUuid().toString(),"Sensor without zone");

        }
        return new SensorResponse(
                entity.getUuid(),
                entity.getName(),
                entity.getStatus(),
                entity.getActivationTime(),
                entity.getMemoryUsed(),
                entity.getDataTransferValue(),
                entity.getDataDescription(),
                entity.getInstallation(),
                zoneUuid,
                entity.getPowerSupply(),
                entity.getVersion(),
                entity.getCreatedBy(),
                entity.getLastModifiedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}