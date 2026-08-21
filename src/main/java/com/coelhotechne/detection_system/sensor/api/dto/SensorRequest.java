package com.coelhotechne.detection_system.sensor.api.dto;

import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.zone.domain.Zone;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SensorRequest(
        @NotBlank
        String name,
        boolean status,
        @NotNull
        @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(
                pattern = "dd/MM/yyyy HH:mm:ss",
                shape = JsonFormat.Shape.STRING
        )
        LocalDateTime activationTime,
        @NotNull
        BigDecimal memoryUsed,
        @NotNull
        BigDecimal dataTransferValue,
        @NotBlank
        String dataDescription,
        LocalDateTime installationDate,
        UUID zoneUUID

) {
    public static Sensor toEntity(SensorRequest request, Zone zone) {
        return new Sensor(
                request.name(),
                request.status(),
                request.activationTime(),
                request.memoryUsed(),
                request.dataTransferValue(),
                request.dataDescription(),
                request.installationDate(),
                zone
        );
    }
}