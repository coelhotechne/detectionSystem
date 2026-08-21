package com.coelhotechne.detection_system.location.api.dto;

import com.coelhotechne.detection_system.location.domain.Location;
import com.coelhotechne.detection_system.zone.domain.Zone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LocationRequest(
        @NotBlank
        String name,
        @NotNull
        BigDecimal latitude,
        @NotNull
        BigDecimal longitude,
        @NotNull
        UUID zoneUuid
) {

    public static Location toEntity(LocationRequest request, Zone zone) {
        return new Location(
                request.name(),
                request.latitude(),
                request.longitude(),
                zone
        );
    }
}