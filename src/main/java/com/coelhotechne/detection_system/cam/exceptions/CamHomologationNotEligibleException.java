package com.coelhotechne.detection_system.cam.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.util.UUID;

public class CamHomologationNotEligibleException extends ErrorResponseException {

    public CamHomologationNotEligibleException(UUID camId, String reason) {
        super(HttpStatus.CONFLICT, asProblemDetail(camId, reason), null);
    }

    private static ProblemDetail asProblemDetail(UUID camId, String reason) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Camera not eligible for this validation process.");
        problemDetail.setProperty("camId", camId);
        problemDetail.setDetail(reason);
        return problemDetail;
    }
}
