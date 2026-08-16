package com.coelhotechne.detection_system.device.exceptions;

import lombok.Getter;

@Getter
public class DeviceNotFoundException extends RuntimeException {
    private final String deviceId;
    private final String deviceName;
    private final String deviceType;

    public DeviceNotFoundException(String deviceId,String deviceName,String deviceType,String message){
        super("Device type : "+deviceType
                +"\nDevice name: "+deviceName
                +"\n Device id: "+deviceId
                +"\nError : "+message);
        this.deviceId=deviceId;
        this.deviceName=deviceName;
        this.deviceType=deviceType;
    }
}
