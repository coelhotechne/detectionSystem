package com.coelhotechne.detection_system.zone.api.dto;

import com.coelhotechne.detection_system.zone.domain.Zone;
import com.coelhotechne.detection_system.zone.domain.enums.ZoneType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ZoneRequest(
        @NotBlank
        String name,
        String description,
        @NotNull
        ZoneType zoneType,
        @NotNull
        BigDecimal centerLatitude,
        @NotNull
        BigDecimal centerLongitude,
        @NotNull
        @Positive
        Double radiusMeters,
        @NotNull
        Boolean active,
        LocalDate createdZoneAtDay

) {

    public static Zone toEntity(ZoneRequest request) {
        if (request == null) {
            return null;
        }
        return new Zone(
                request.name(),
                request.description(),
                request.zoneType(),
                request.centerLatitude(),
                request.centerLongitude(),
                request.radiusMeters(),
                request.active(),
                request.createdZoneAtDay()
        );
    }
}