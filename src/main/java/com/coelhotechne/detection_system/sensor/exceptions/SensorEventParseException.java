package com.coelhotechne.detection_system.sensor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class SensorEventParseException extends ErrorResponseException {

    public SensorEventParseException(String reason, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, buildProblemDetail(reason), cause);
    }

    private static ProblemDetail buildProblemDetail(String reason) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, reason);
        pd.setTitle("Invalid sensor event payload");
        return pd;
    }
}
