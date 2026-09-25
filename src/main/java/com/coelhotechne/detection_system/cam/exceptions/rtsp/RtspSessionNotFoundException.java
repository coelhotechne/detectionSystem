package com.coelhotechne.detection_system.cam.exceptions.rtsp;

public class RtspSessionNotFoundException extends RtspException{
    public RtspSessionNotFoundException(String message) {
        super(504,message);
    }
}
