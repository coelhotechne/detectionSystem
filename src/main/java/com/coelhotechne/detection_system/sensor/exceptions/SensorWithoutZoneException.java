package com.coelhotechne.detection_system.sensor.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class SensorWithoutZoneException extends ErrorResponseException {
    private final String sensorId;

    public SensorWithoutZoneException(String sensorId, String reason) {
        super(HttpStatus.CONFLICT, buildProblemDetail(sensorId, reason), null);
        this.sensorId = sensorId;
    }

    private static ProblemDetail buildProblemDetail(String sensorId, String reason) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, reason);
        pd.setTitle("Sensor sem zona");
        pd.setProperty("sensorId", sensorId);
        return pd;
    }
}
