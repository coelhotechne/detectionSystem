package com.coelhotechne.detection_system.sensor.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class SensorUnavailableException extends ErrorResponseException {
    private final String sensorId;
    private final String sensorName;
    private final boolean sensorStatus;

    public SensorUnavailableException(String sensorId,String sensorName,boolean sensorStatus,String reason) {
        super(HttpStatus.NOT_FOUND, buildProblemDetail(sensorId,sensorName,sensorStatus,reason),null);
        this.sensorId=sensorId;
        this.sensorName=sensorName;
        this.sensorStatus=sensorStatus;
    }

    private static ProblemDetail buildProblemDetail(String sensorId, String sensorName, boolean sensorStatus, String reason){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,reason);
        pd.setTitle("Sensor Unavailable!");
        pd.setProperty("sensorId",sensorId);
        pd.setProperty("sensorName",sensorName);
        pd.setProperty("sensorStatus",sensorStatus);
        return pd;
    }
}
