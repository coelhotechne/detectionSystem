package com.coelhotechne.detection_system.cam.exceptions.rtsp;

public class RtspException extends RuntimeException {
    private final int statusCode;

    public RtspException(int statusCode,String cause) {
        super(cause);
        this.statusCode=statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
