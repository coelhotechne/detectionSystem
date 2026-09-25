package com.coelhotechne.detection_system.cam.exceptions.rtsp;

public class RtspNotFoundException extends RtspException {

    public RtspNotFoundException(String cause){
        super(404,cause);
    }
}
