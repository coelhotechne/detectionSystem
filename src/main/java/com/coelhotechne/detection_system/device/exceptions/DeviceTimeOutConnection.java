package com.coelhotechne.detection_system.device.exceptions;

import com.coelhotechne.detection_system.device.domain.DeviceSession;

import java.net.ConnectException;

public class DeviceTimeOutConnection extends ConnectException {
    public DeviceTimeOutConnection(DeviceSession deviceSession, String message) {

        super("Device message : "+deviceSession.getDeviceMessage()
                +" Time: "+deviceSession.getTimestamp()
                +" Error :"+message);
    }
}
