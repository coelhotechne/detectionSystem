package com.coelhotechne.detection_system.detection.domain;

import com.coelhotechne.detection_system.detection.domain.enums.SensorDetectionType;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "sensor_detection")
@AllArgsConstructor
public class SensorDetection extends Detection{
    @Enumerated(value = EnumType.STRING)
    private SensorDetectionType sensorDetectionType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;
}
