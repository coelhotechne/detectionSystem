package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.api.dto.SensorPatchRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorReplaceRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorCommand;
import com.coelhotechne.detection_system.sensor.event.SensorEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SensorService {

    Sensor requireSensor(UUID sensorId);
    Page<SensorResponse> findSensorPageList(Pageable pageable);
    SensorResponse findSensorId(UUID uuid);
    SensorResponse createSensor(SensorRequest sensorRequest);
    SensorResponse replaceSensor(UUID uuid, SensorReplaceRequest request, Long expectedVersion);
    SensorResponse patchSensor(UUID uuid, SensorPatchRequest patch, Long expectedVersion);
    SensorResponse deleteSensor(UUID uuid);

    //::::::::::::::::::::Intern processing::::::::::::::::::::::::::::::::::
    SensorResponse requestMaintenance(UUID uuid);
    SensorResponse clearMaintenance(UUID uuid);
    void process(SensorEvent event);
    void sendCommand(UUID sensorId, SensorCommand sensorCommand);
}
