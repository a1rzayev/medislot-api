package com.medislot.medislot.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DoctorCreateRequest {
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Specialty is required")
    @Size(min = 2, max = 50, message = "Specialty must be between 2 and 50 characters")
    private String specialty;

    private Boolean active = true;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
