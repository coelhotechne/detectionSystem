package com.coelhotechne.detection_system.sensor.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class SensorStillActiveException extends ErrorResponseException {
    private final String sensorId;
    private final boolean status;
    public SensorStillActiveException(String sensorId, boolean status, String reason){
        super(HttpStatus.CONFLICT,buildProblemDetail(sensorId,status,reason),null);
        this.sensorId=sensorId;
        this.status=status;
    }
    private static ProblemDetail buildProblemDetail(String sensorId,boolean status,String reason){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,reason);
        pd.setTitle("Sensor in use exception");
        pd.setProperty("sensorId",sensorId);
        pd.setProperty("status",status);
        return pd;
    }
}
