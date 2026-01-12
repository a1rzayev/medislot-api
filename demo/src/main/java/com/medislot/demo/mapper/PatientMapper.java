package com.medislot.demo.mapper;

import com.medislot.demo.dto.patient.PatientCreateRequest;
import com.medislot.demo.dto.patient.PatientResponse;
import com.medislot.demo.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientResponse toResponse(Patient patient) {
        if (patient == null) {
            return null;
        }

        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setFullName(patient.getFullName());
        response.setPhone(patient.getPhone());
        response.setEmail(patient.getEmail());
        response.setActive(patient.getActive());
        response.setCreatedAt(patient.getCreatedAt());
        return response;
    }

    public Patient toEntity(PatientCreateRequest request) {
        if (request == null) {
            return null;
        }

        Patient patient = new Patient();
        patient.setFullName(request.getFullName());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setActive(request.getActive() != null ? request.getActive() : true);
        return patient;
    }
}
