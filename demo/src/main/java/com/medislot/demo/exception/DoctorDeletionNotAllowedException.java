package com.medislot.demo.exception;

public class DoctorDeletionNotAllowedException extends RuntimeException {
    public DoctorDeletionNotAllowedException(String message) {
        super(message);
    }

    public DoctorDeletionNotAllowedException() {
        super("Cannot delete doctor with existing slots or appointments. Deactivate instead.");
    }
}
