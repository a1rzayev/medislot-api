package com.medislot.demo.dto.hospital;

import jakarta.validation.constraints.NotBlank;

public class HospitalCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String address;

    private Boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
