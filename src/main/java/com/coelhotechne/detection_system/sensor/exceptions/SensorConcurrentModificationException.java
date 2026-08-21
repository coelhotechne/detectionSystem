package com.coelhotechne.detection_system.sensor.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class SensorConcurrentModificationException extends ErrorResponseException {
    private final String sensorId;

    public SensorConcurrentModificationException(String sensorId,Throwable cause){
        super(HttpStatus.CONFLICT,buildProblemDetail(sensorId),cause);
        this.sensorId=sensorId;
    }


    private static ProblemDetail buildProblemDetail(String sensorId){
    ProblemDetail pd=ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT
            ,"The sensor was modified by another request; please try again.");

    pd.setTitle("Concurrent Modification");
    pd.setProperty("sensorId",sensorId);
    return pd;
    }

}
