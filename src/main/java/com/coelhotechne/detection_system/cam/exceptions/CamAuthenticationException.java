package com.coelhotechne.detection_system.cam.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.util.UUID;

public class CamAuthenticationException extends ErrorResponseException {

    public CamAuthenticationException(UUID camId) {
        super(HttpStatus.UNAUTHORIZED, asProblemDetail(camId), null);
    }

    private static ProblemDetail asProblemDetail(UUID camId) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Cam Authentication Failure");
        problemDetail.setProperty("camId", camId);
        return problemDetail;
    }
}