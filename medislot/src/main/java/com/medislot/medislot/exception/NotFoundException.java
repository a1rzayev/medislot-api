package com.medislot.medislot.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resource, Object id) {
        super(String.format("%s with id %s not found", resource, id));
    }
}
