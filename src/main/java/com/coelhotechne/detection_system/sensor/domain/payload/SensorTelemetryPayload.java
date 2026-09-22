package com.coelhotechne.detection_system.sensor.domain.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorTelemetryPayload (
        @JsonProperty("status")
        Boolean status,
        @JsonProperty("memoru_used")
        BigDecimal memoryUsed,
        @JsonProperty("data_transfer_value")
        BigDecimal dataTransferValue,
        @JsonProperty("data_description")
        String dataDescription,
        @JsonProperty("power_percentege")
        BigDecimal powerPercentege,
        @JsonProperty("power_charging")
        Boolean powerCharging
){
    public Boolean hasPowerReading(){
        return powerPercentege!=null || powerCharging!=null;
    }
}
