package com.coelhotechne.detection_system.zone.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ZoneNotFoundException extends ErrorResponseException {
    private final String zoneId;
    public ZoneNotFoundException(String zoneId,String reason) {
        super(HttpStatus.NOT_FOUND,buildProblemDetail(zoneId,reason),null);
        this.zoneId=zoneId;

    }
    private static ProblemDetail buildProblemDetail(String zoneId,String reason){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,reason);
        pd.setTitle("Zone Not Found");
        pd.setProperty("zoneId",zoneId);
        return pd;
    }
}
