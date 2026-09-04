package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.event.SensorEvent;

import java.util.List;
import java.util.UUID;

public interface SensorService {
    Sensor requireSensor(UUID sensorId);
    List<SensorResponse> findSensorList();
    SensorResponse findSensorId(UUID uuid);
    SensorResponse createSensor(SensorRequest sensorRequest);
    SensorResponse updateSensor(UUID uuid, SensorRequest sensorRequest);
    SensorResponse deleteSensor(UUID uuid);
    void process(SensorEvent event);
    void sendCommand(UUID sensorId, String action);
}
