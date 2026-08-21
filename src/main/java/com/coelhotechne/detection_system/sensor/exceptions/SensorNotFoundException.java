package com.coelhotechne.detection_system.sensor.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class SensorNotFoundException extends ErrorResponseException {
    private final String sensorId;
    public SensorNotFoundException(String sensorId,String reason) {
        super(HttpStatus.NOT_FOUND, buildProblemDetail(sensorId,reason),null);
        this.sensorId=sensorId;
    }

    private static ProblemDetail buildProblemDetail(String sensorId, String reason){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,reason);
        pd.setTitle("Sensor not found!");
        pd.setProperty("sensorId",sensorId);
        return pd;
    }
}
