package com.coelhotechne.detection_system.cam.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Getter
public class CamConcurrentModificationException extends ErrorResponseException {
    private final String camId;
    public CamConcurrentModificationException(String camId, String cause){
        super(HttpStatus.CONFLICT,buildProblemDetail(camId,cause),null);
        this.camId=camId;
    }

    private static ProblemDetail buildProblemDetail(String camId, String cause){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, cause);
        pd.setTitle("Concurrent Modification");
        pd.setProperty("Id",camId);

        return pd;
    }
}
