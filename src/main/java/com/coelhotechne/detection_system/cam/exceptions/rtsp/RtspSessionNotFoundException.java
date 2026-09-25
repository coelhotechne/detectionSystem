package com.coelhotechne.detection_system.cam.exceptions.rtsp;

public class RtspSessionNotFoundException extends RtspException{
    public RtspSessionNotFoundException(String cause) {
        super(504,cause);
    }
}
