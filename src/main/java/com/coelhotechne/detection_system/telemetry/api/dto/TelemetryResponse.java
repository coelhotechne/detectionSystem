package com.coelhotechne.detection_system.telemetry.api.dto;

import com.coelhotechne.detection_system.telemetry.domain.Telemetry;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

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
}