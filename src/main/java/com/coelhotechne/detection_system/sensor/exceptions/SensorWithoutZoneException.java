package com.coelhotechne.detection_system.sensor.exceptions;

public class SensorWithoutZoneException extends RuntimeException {
    private final String id;
    public SensorWithoutZoneException(String id, String message) {
        super(message);
        this.id = id;
    }
}
