package com.coelhotechne.detection_system.telemetry.api.dto;

import com.coelhotechne.detection_system.telemetry.domain.Telemetry;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.format.annotation.DateTimeFormat;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({
        "uuid",
        "createdBy",
        "lastModifiedBy",
        "createdAt",
        "updatedAt",
        "zoneId",
        "sensorId",
        "measuredValue",
        "measuredAt"
})
public record TelemetryResponse(

        UUID uuid,
        Long version,
        String createdBy,
        String lastModifiedBy,

        @DateTimeFormat(pattern = "dd/MM/yyyy")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime createdAt,

        @DateTimeFormat(pattern = "dd/MM/yyyy")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime updatedAt,

        UUID zoneId,

        UUID sensorId,

        Integer measuredValue,

        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "dd/MM/yyyy HH:mm:ss",
                timezone = "UTC"
        )
        Instant measuredAt

) {

    public static TelemetryResponse toResponse(Telemetry telemetry) {

        return new TelemetryResponse(
                telemetry.getUuid(),
                telemetry.getVersion(),
                telemetry.getCreatedBy(),
                telemetry.getLastModifiedBy(),
                telemetry.getCreatedAt(),
                telemetry.getUpdatedAt(),
                telemetry.getZone().getUuid(),
                telemetry.getSensor().getUuid(),
                telemetry.getMeasuredValue(),
                telemetry.getMeasuredAt()
        );
    }
}