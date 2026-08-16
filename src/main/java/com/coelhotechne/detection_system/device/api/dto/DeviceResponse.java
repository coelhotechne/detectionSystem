package com.coelhotechne.detection_system.device.api.dto;

import com.coelhotechne.detection_system.device.domain.Device;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.format.annotation.DateTimeFormat;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonPropertyOrder({
        "uuid",
        "createdBy",
        "lastModifiedBy",
        "createdAt",
        "updatedAt",
        "deviceName",
        "deviceType",
        "ipAddress",
        "status",
        "lastCommunication"
})
public record DeviceResponse(
        UUID uuid,
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
        String deviceName,
        String deviceType,
        String ipAddress,
        Boolean status,
        LocalDateTime lastCommunication

) {
    public static DeviceResponse toResponse(Device device){
        return new DeviceResponse(
                device.getUuid(),
                device.getCreatedBy(),
                device.getLastModifiedBy(),
                device.getCreatedAt(),
                device.getUpdatedAt(),
                device.getDeviceName(),
                device.getDeviceType(),
                device.getIpAdress(),
                device.getStatus(),
                device.getLastCommunication()
        );
    }
}
