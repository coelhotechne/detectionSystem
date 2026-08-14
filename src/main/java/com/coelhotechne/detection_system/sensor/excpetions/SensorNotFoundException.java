package com.coelhotechne.detection_system.sensor.excpetions;

import lombok.Getter;

@Getter
public class SensorNotFoundException extends RuntimeException {
    private final String sensorId;
    private final String sensorName;
    private final boolean sensorStatus;
    public SensorNotFoundException(String sensorId,String sensorName,boolean sensorStatus,String message) {
        super( "\nSensor id : "+sensorId
                +"\nSensor name :"+sensorName
                +"\nSensor status :"+sensorStatus
                +"\nError :"+message);
        this.sensorId=sensorId;
        this.sensorName=sensorName;
        this.sensorStatus=sensorStatus;
    }
}
