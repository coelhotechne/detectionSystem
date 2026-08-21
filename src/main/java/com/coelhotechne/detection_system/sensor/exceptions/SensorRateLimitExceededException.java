package com.coelhotechne.detection_system.sensor.exceptions;

import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class SensorRateLimitExceededException extends ErrorResponseException {
    private final long retryAfterSeconds;
    public SensorRateLimitExceededException(String sensorId, long retryAfterSeconds) {
        super(HttpStatus.TOO_MANY_REQUESTS,buildProblemDetail(sensorId,retryAfterSeconds),null);
        this.retryAfterSeconds=retryAfterSeconds;
    }

    private static ProblemDetail buildProblemDetail(String sensorId, long retryAfterSeconds){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS
                , "Request's rate limit exceeded for Sensor with id: "+sensorId);

        pd.setTitle("Rate Limit Exceeded");
        pd.setProperty("sensorId",sensorId);
        pd.setProperty("retryAfterSeconds",retryAfterSeconds);
        return pd;
    }
    @Override
    public HttpHeaders getHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.RETRY_AFTER,String.valueOf(retryAfterSeconds));
        return httpHeaders;
    }
}
