package com.coelhotechne.detection_system.sensor.application.event;

import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorNiche;
import com.coelhotechne.detection_system.sensor.domain.payload.SensorDetectionPayload;

public interface SensorNicheReadingHandler {
    SensorNiche niche();
    void handler(Sensor sensor, SensorDetectionPayload detectionPayload);
}
