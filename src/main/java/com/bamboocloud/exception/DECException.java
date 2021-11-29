package com.bamboocloud.exception;

public class DECException extends Exception {
    public DECException(String message) {
        super(message);
    }

    public DECException(String message, Throwable cause) {
        super(message, cause);
    }
}
