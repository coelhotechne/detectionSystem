package com.coelhotechne.detection_system.sensor.api.dto;


import com.coelhotechne.detection_system.sensor.domain.Sensor;
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
import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({
        "uuid",
        "name",
        "status",
        "activationTime",
        "memoryUsed",
        "dataTransferValue",
        "dataDescription",
        "createdBy",
        "lastModifiedBy",
        "createdAt",
        "updatedAt"
})
public record SensorResponse(
        UUID uuid,
        String name,
        boolean status,
        @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(
                pattern = "dd/MM/yyyy HH:mm:ss",
                shape = JsonFormat.Shape.STRING
        )
        LocalDateTime activationTime,
        BigDecimal memoryUsed,
        BigDecimal dataTransferValue,
        String dataDescription,
        @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(
                pattern = "dd/MM/yyyy HH:mm:ss",
                shape = JsonFormat.Shape.STRING
        )
        LocalDateTime installationData,
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
    public static SensorResponse toResponse(Sensor sensor) {
        if (sensor == null) {
            return null;
        }
        return new SensorResponse(
                sensor.getUuid(),
                sensor.getName(),
                sensor.isStatus(),
                sensor.getActivationTime(),
                sensor.getMemoryUsed(),
                sensor.getDataTransferValue(),
                sensor.getDataDescription(),
                sensor.getInstallationData(),
                sensor.getCreatedBy(),
                sensor.getLastModifiedBy(),
                sensor.getCreatedAt(),
                sensor.getUpdatedAt()
        );
    }
}
