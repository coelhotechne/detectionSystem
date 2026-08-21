package com.coelhotechne.detection_system.sensor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class SensorAccessDeniedException extends ErrorResponseException {
    private final String sensorId;

    public SensorAccessDeniedException(String sensorId){
        super(HttpStatus.FORBIDDEN,buildProblemDetail(sensorId),null);
        this.sensorId=sensorId;
    }

    private static ProblemDetail buildProblemDetail(String sensorId){
        ProblemDetail pd= ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource.");
        pd.setTitle("Access Denied");
        pd.setProperty("sensorId",sensorId);
        return pd;
    }
}
