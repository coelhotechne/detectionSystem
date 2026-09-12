package com.coelhotechne.detection_system.location.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class LocationIdNotFoundException extends ErrorResponseException {
    private final String locationId;
    public LocationIdNotFoundException(String locationId,String reason) {
        super(HttpStatus.NOT_FOUND,buildProblemDetail(locationId,reason),null);
        this.locationId =locationId;
    }

    private static ProblemDetail buildProblemDetail(String locationId, String reason){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,reason);
        pd.setTitle("Location ID Not Found");
        pd.setProperty("locationId",locationId);
        return pd;
    }
}
