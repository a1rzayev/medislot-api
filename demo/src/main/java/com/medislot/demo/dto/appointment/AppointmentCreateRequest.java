package com.medislot.demo.dto.appointment;

import com.medislot.demo.entity.AppointmentStatus;
import com.medislot.demo.validation.ValidAppointmentTime;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@ValidAppointmentTime
public class AppointmentCreateRequest {
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;

    @NotNull(message = "Hospital ID is required")
    private UUID hospitalId;

    @NotNull(message = "Slot ID is required")
    private UUID slotId;

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    private AppointmentStatus status = AppointmentStatus.BOOKED;

    public UUID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }

    public UUID getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(UUID hospitalId) {
        this.hospitalId = hospitalId;
    }

    public UUID getSlotId() {
        return slotId;
    }

    public void setSlotId(UUID slotId) {
        this.slotId = slotId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
