package com.selfdrive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SelfDriveExceptionHandler {

    @ExceptionHandler(value = SelfDriveException.class)
    public ResponseEntity<UserError> handleSelfDriveException(SelfDriveException e) {

        if (e.getStatusCode() == 404) {
            return new ResponseEntity<>(
                    UserError.builder().message("File not found").providerMessage(e.getProviderMessage()).build(),
                    HttpStatus.NOT_FOUND
            );
        }
        if (e.getStatusCode() == 401) {
            return new ResponseEntity<>(
                    UserError.builder().message("Unauthorized").providerMessage(e.getProviderMessage()).build(),
                    HttpStatus.UNAUTHORIZED
            );
        }
        return null;
    }
}
