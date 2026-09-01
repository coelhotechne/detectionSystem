package com.coelhotechne.detection_system.sensor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.util.UUID;

public class SensorCommandDeliveryException extends ErrorResponseException {
    public SensorCommandDeliveryException(UUID uuid, Throwable cause) {
        super(HttpStatus.SERVICE_UNAVAILABLE,buildProblemDetail(uuid),cause);
    }

    private static ProblemDetail buildProblemDetail(UUID uuid){
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);

        pd.setTitle("Failed to deliver the command!");
        pd.setDetail("Not possible to publish the command for the %s sensor in broker MQTT."
                .formatted(uuid));
        // ajustar pro esquema de URIs no catálogo de erros
        pd.setType(URI.create("https://coelhotechne.com/errors/sensor/command-delivery-failed"));
        pd.setProperty("sensorId", uuid);
        return pd;
    }
}
