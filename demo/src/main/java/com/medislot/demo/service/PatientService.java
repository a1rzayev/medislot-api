package com.medislot.demo.service;

import com.medislot.demo.dto.patient.PatientCreateRequest;
import com.medislot.demo.dto.patient.PatientResponse;
import com.medislot.demo.entity.Patient;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Patient operations
 */
public interface PatientService extends BaseService<Patient, UUID, PatientCreateRequest, PatientCreateRequest, PatientResponse> {
    
    /**
     * Find all active patients
     * @return list of active patient responses
     */
    List<PatientResponse> findAllActive();
    
    /**
     * Deactivate a patient (soft delete)
     * @param id the patient ID
     * @return the deactivated patient response
     */
    PatientResponse deactivate(UUID id);
    
    /**
     * Activate a patient
     * @param id the patient ID
     * @return the activated patient response
     */
    PatientResponse activate(UUID id);
}
