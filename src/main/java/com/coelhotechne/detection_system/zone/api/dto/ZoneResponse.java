package com.coelhotechne.detection_system.zone.api.dto;

import com.coelhotechne.detection_system.zone.domain.Zone;
import com.coelhotechne.detection_system.zone.domain.enums.ZoneType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.format.annotation.DateTimeFormat;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({
        "uuid",
        "name",
        "description",
        "zoneType",
        "centerLatitude",
        "centerLongitude",
        "radiusMeters",
        "active",
        "createdBy",
        "lastModifiedBy",
        "createdAt",
        "updatedAt"
})
public record ZoneResponse(
        UUID uuid,
        String name,
        String description,
        ZoneType zoneType,
        BigDecimal centerLatitude,
        BigDecimal centerLongitude,
        Double radiusMeters,
        Boolean active,
        LocalDate createdZoneAtDay,
        String createdBy,
        String lastModifiedBy,
        @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(
                pattern = "dd/MM/yyyy HH:mm:ss",
                shape = JsonFormat.Shape.STRING
        )
        LocalDateTime createdAt,
        @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(
                pattern = "dd/MM/yyyy HH:mm:ss",
                shape = JsonFormat.Shape.STRING
        )
        LocalDateTime updatedAt
) {

    public static ZoneResponse toResponse(Zone zone) {
        if (zone == null) {
            return null;
        }
        return new ZoneResponse(
                zone.getUuid(),
                zone.getName(),
                zone.getDescription(),
                zone.getZoneType(),
                zone.getCenterLatitude(),
                zone.getCenterLongitude(),
                zone.getRadiusMeters(),
                zone.getActive(),
                zone.getCreatedZoneAtDay(),
                zone.getCreatedBy(),
                zone.getLastModifiedBy(),
                zone.getCreatedAt(),
                zone.getUpdatedAt()
        );
    }
}