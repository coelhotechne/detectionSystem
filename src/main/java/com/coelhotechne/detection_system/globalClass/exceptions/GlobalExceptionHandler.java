package com.coelhotechne.detection_system.globalClass.exceptions;

import org.springframework.http.*;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<Object> handleErrorResponse(ErrorResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .headers(ex.getHeaders())
                .body(ex.getBody());
    }

    // Rede de segurança para quando o @PreUpdate/@PrePersist é quem barra —
    // só deveria disparar se algum caminho de código bypassar o ZoneService.requireZone()
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Object> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof IllegalStateException) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, rootCause.getMessage());
            return ResponseEntity.unprocessableEntity().body(pd);
        }
        throw ex;
    }
}
