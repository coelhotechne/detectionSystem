package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SensorService {
    List<SensorResponse> findSensorList();
    SensorResponse findSensorId(UUID uuid);
    SensorResponse createSensor(SensorRequest sensorRequest);
    SensorResponse updateSensor(UUID uuid, SensorRequest sensorRequest);
    SensorResponse deleteSensor(UUID uuid);
}
