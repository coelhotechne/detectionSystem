package com.coelhotechne.detection_system.sensor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class UnsupportedSensorEventException extends ErrorResponseException {

    public UnsupportedSensorEventException(String eventType) {
        super(HttpStatus.BAD_REQUEST, buildProblemDetail(eventType), null);
    }

    private static ProblemDetail buildProblemDetail(String eventType) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Unsupported sensor event type: " + eventType);
        pd.setTitle("Unsupported Sensor Event");
        pd.setProperty("eventType", eventType);
        return pd;
    }
}
