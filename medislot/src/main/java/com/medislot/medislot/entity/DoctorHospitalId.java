package com.medislot.medislot.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class DoctorHospitalId implements Serializable {

    private UUID doctorId;
    private UUID hospitalId;

    public DoctorHospitalId() {
    }

    public DoctorHospitalId(UUID doctorId, UUID hospitalId) {
        this.doctorId = doctorId;
        this.hospitalId = hospitalId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoctorHospitalId that = (DoctorHospitalId) o;
        return Objects.equals(doctorId, that.doctorId) &&
                Objects.equals(hospitalId, that.hospitalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, hospitalId);
    }
}

