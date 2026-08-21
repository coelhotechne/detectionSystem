package com.coelhotechne.detection_system.location.api.dto;

import com.coelhotechne.detection_system.globalClass.mapper.GenericMapper;
import com.coelhotechne.detection_system.location.domain.Location;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LocationMapper implements GenericMapper<Location,LocationResponse,LocationRequest> {
    @Override
    public Location toEntity(LocationRequest request) {
        Objects.requireNonNull(request,"Request cannot be null!");
        Location entity = new Location();
        entity.setName(request.name());
        entity.setLatitude(request.latitude());
        entity.setLongitude(request.longitude());
        return entity;
    }

    @Override
    public LocationResponse toResponse(Location entity) {
        Objects.requireNonNull(entity,"Entity cannot be null");
        return new LocationResponse(
                entity.getUuid(),
                entity.getName(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getZone().getUuid(),
                entity.getCreatedBy(),
                entity.getLastModifiedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
