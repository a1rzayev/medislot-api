package com.medislot.medislot.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("Access to this resource is forbidden");
    }
}
