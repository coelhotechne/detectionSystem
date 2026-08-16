package com.coelhotechne.detection_system.device.exceptions;

import com.coelhotechne.detection_system.device.domain.enums.DeviceMessage;

public class DeviceAuthenticationException extends RuntimeException {
    private final String deviceId;
    private final DeviceMessage deviceMessage;
    private final String message;

    public DeviceAuthenticationException(String deviceId,DeviceMessage deviceMessage,String message) {
        super("\n Device id : "+deviceId
                +"\n Device Message : "+deviceMessage
                +"\nError : "+message);
        this.deviceId=deviceId;
        this.deviceMessage =deviceMessage;
        this.message = message;
    }
}
