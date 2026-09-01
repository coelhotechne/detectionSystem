package com.coelhotechne.detection_system.sensor.api.dto;


import com.coelhotechne.detection_system.batterysupply.domain.PowerSupply;
import com.coelhotechne.detection_system.installation.domain.Installation;
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
        "sensorStatus",
        "activationTime",
        "memoryUsed",
        "dataTransferValue",
        "dataDescription",
        "installation",
        "sensorBattery",
        "zoneUuid",
        "version",
        "createdBy",
        "lastModifiedBy",
        "createdAt",
        "updatedAt"
})
public record SensorResponse(
        UUID uuid,
        String name,
        boolean sensorStatus,
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
        Installation installation,
        UUID zoneUuid,
        PowerSupply powerSupply,
        Long version,
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
        return new SensorResponse(
                sensor.getUuid(),
                sensor.getName(),
                sensor.getSensorStatus(),
                sensor.getActivationTime(),
                sensor.getMemoryUsed(),
                sensor.getDataTransferValue(),
                sensor.getDataDescription(),
                sensor.getInstallation(),
                sensor.getZone().getUuid(),
                sensor.getPowerSupply(),
                sensor.getVersion(),
                sensor.getCreatedBy(),
                sensor.getLastModifiedBy(),
                sensor.getCreatedAt(),
                sensor.getUpdatedAt()
        );
    }
}
