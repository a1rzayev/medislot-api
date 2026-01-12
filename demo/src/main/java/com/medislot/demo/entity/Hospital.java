package com.medislot.demo.entity;

import com.medislot.demo.entity.base.BaseEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hospitals")
public class Hospital extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "hospital")
    private Set<DoctorHospital> doctors = new HashSet<>();

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

    public Set<DoctorHospital> getDoctors() {
        return doctors;
    }

    public void setDoctors(Set<DoctorHospital> doctors) {
        this.doctors = doctors;
    }
}

