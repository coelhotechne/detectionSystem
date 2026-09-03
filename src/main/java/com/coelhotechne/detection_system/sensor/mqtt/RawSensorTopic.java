package com.coelhotechne.detection_system.sensor.mqtt;

public record RawSensorTopic(String zoneName, String sensorName, String eventType) {

    private static final int EXPECTED_SEGMENTS = 4;

    public static RawSensorTopic parse(String rawTopic) {
        String[] parts = rawTopic.split("/");
        if (parts.length < EXPECTED_SEGMENTS) {
            throw new IllegalArgumentException("Unexpected mqtt topic shape: " + rawTopic);
        }
        return new RawSensorTopic(parts[1], parts[2], parts[3]);
    }
}
