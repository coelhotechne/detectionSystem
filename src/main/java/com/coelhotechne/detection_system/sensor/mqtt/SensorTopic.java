package com.coelhotechne.detection_system.sensor.mqtt;

import java.util.UUID;

public record SensorTopic(UUID sensorId, String eventType) {
}
