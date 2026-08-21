package com.coelhotechne.detection_system.location.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.math.BigDecimal;

@Getter
public class LocationNotFoundException extends ErrorResponseException {
    private final String locationId;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    public LocationNotFoundException(String locationId,BigDecimal latitude,BigDecimal longitude,String reason) {
        super(HttpStatus.NOT_FOUND,buildProblemDetail(locationId,latitude,longitude,reason),null);
        this.locationId =locationId;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    private static ProblemDetail buildProblemDetail(String locationId,BigDecimal latitude,BigDecimal longitude,String reason){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,reason);
        pd.setTitle("Location not found");
        pd.setProperty("locationId",locationId);
        pd.setProperty("latitude",latitude);
        pd.setProperty("longitude",longitude);
        return pd;
    }
}
