package com.coelhotechne.detection_system.telemetry.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class TelemetryConcurrentModificationException extends ErrorResponseException {
    private final String telemetryId;

    public TelemetryConcurrentModificationException(String telemetryId, String cause) {
        super(HttpStatus.CONFLICT, buildProblemDetail(telemetryId, cause), null);
        this.telemetryId = telemetryId;
    }

    private static ProblemDetail buildProblemDetail(String telemetryId, String cause) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, cause);
        pd.setTitle("Concurrent Modification");
        pd.setProperty("id", telemetryId);
        return pd;
    }
}
