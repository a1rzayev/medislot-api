package com.medislot.medislot.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException() {
        super("Resource conflict detected");
    }
}
