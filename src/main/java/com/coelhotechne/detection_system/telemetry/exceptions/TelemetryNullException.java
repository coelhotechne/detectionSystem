package com.coelhotechne.detection_system.telemetry.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class TelemetryNullException extends ErrorResponseException {
    private final String telemetryid;

    public TelemetryNullException(String telemetryid, String cause){
        super(HttpStatus.NOT_FOUND,buildProblemDetail(telemetryid,cause),null);
        this.telemetryid=telemetryid;
    }

    private static ProblemDetail buildProblemDetail(String telemetryid, String cause){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,cause);
        pd.setTitle("Null return, please check");
        pd.setProperty("id",telemetryid);
        return pd;
    }
}
