package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;
import com.coelhotechne.detection_system.sensor.api.dto.SensorTelemetryPayload;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SensorService {
    List<SensorResponse> findSensorList();
    SensorResponse findSensorId(UUID uuid);
    SensorResponse createSensor(SensorRequest sensorRequest);
    SensorResponse updateSensor(UUID uuid, SensorRequest sensorRequest);
    SensorResponse deleteSensor(UUID uuid);
    void applyTelemetry(String zoneName, String sensorName, SensorTelemetryPayload payload,Boolean sensorStatus);
    void sendCommand(UUID sensorId, String action);
}
