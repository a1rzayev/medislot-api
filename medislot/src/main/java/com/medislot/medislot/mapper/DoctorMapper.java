package com.medislot.medislot.mapper;

import com.medislot.medislot.dto.doctor.DoctorCreateRequest;
import com.medislot.medislot.dto.doctor.DoctorResponse;
import com.medislot.medislot.dto.doctor.DoctorUpdateRequest;
import com.medislot.medislot.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public DoctorResponse toResponse(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        DoctorResponse response = new DoctorResponse();
        response.setId(doctor.getId());
        response.setFullName(doctor.getFullName());
        response.setSpecialty(doctor.getSpecialty());
        response.setActive(doctor.getActive());
        response.setCreatedAt(doctor.getCreatedAt());
        return response;
    }

    public Doctor toEntity(DoctorCreateRequest request) {
        if (request == null) {
            return null;
        }

        Doctor doctor = new Doctor();
        doctor.setFullName(request.getFullName());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setActive(request.getActive() != null ? request.getActive() : true);
        return doctor;
    }

    public void updateEntity(Doctor doctor, DoctorUpdateRequest request) {
        if (doctor == null || request == null) {
            return;
        }

        if (request.getFullName() != null) {
            doctor.setFullName(request.getFullName());
        }
        if (request.getSpecialty() != null) {
            doctor.setSpecialty(request.getSpecialty());
        }
        if (request.getActive() != null) {
            doctor.setActive(request.getActive());
        }
    }
}
