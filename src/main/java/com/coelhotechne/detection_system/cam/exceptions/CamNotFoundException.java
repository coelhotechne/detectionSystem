package com.coelhotechne.detection_system.cam.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class CamNotFoundException extends ErrorResponseException {

    private final String camId;

    public CamNotFoundException(String camId, String cause){
        super(HttpStatus.NOT_FOUND,buildProblemDetail(camId,cause),null);
        this.camId=camId;
    }

    private static ProblemDetail buildProblemDetail(String camId,String cause){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,cause);
        pd.setTitle("Cam Id Not Found");
        pd.setProperty("Id",camId);

        return pd;
    }
}
