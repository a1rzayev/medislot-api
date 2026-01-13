package com.medislot.medislot.entity;

/**
 * User roles for authorization
 * ADMIN: Can manage hospitals and doctors
 * DOCTOR: Can manage their own slots and appointments
 * PATIENT: Can create and manage their own appointments
 */
public enum Role {
    ADMIN,
    DOCTOR,
    PATIENT
}
