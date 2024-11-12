package com.openframe.openframe.common;

import com.openframe.openframe.exception.security.SecurityCustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityCustomException.class)
    public ResponseEntity<ApplicationResponse<String>> handleSecurityException(SecurityCustomException ex) {
        ApplicationResponse<String> response = new ApplicationResponse<>(
                new ApplicationResult(Integer.parseInt(ex.getErrorCode().getCode()), ex.getErrorCode().getMessage()),
                null
        );
        return new ResponseEntity<>(response, ex.getErrorCode().getHttpStatus());
    }
}
