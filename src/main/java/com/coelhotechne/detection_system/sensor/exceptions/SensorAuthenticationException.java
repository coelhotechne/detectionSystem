package com.coelhotechne.detection_system.sensor.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class SensorAuthenticationException extends ErrorResponseException {
    public SensorAuthenticationException(String sensorId, String message) {
        super(HttpStatus.UNAUTHORIZED, buildProblemDetail(sensorId, message), null);
    }

    private static ProblemDetail buildProblemDetail(String sensorId, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, message);
        problemDetail.setTitle("Sensor authentication field");
        problemDetail.setProperty("sensorId", sensorId);
        return problemDetail;
    }
}
