package com.selfdrive.exception;

import lombok.Data;

@Data
public class SelfDriveException extends RuntimeException {

    int statusCode;
    String providerMessage;

    public SelfDriveException( String message) {
        super(message);
    }

    public SelfDriveException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public SelfDriveException(String message, int statusCode, String providerMessage) {
        super(message);
        this.statusCode = statusCode;
        this.providerMessage = providerMessage;
    }
}
