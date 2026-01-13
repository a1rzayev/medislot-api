package com.medislot.medislot.service;

import com.medislot.medislot.dto.doctor.DoctorCreateRequest;
import com.medislot.medislot.dto.doctor.DoctorResponse;
import com.medislot.medislot.dto.doctor.DoctorUpdateRequest;
import com.medislot.medislot.entity.Doctor;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Doctor operations
 */
public interface DoctorService extends BaseService<Doctor, UUID, DoctorCreateRequest, DoctorUpdateRequest, DoctorResponse> {
    
    /**
     * Find all active doctors
     * @return list of active doctor responses
     */
    List<DoctorResponse> findAllActive();
    
    /**
     * Deactivate a doctor (soft delete)
     * @param id the doctor ID
     * @return the deactivated doctor response
     */
    DoctorResponse deactivate(UUID id);
    
    /**
     * Activate a doctor
     * @param id the doctor ID
     * @return the activated doctor response
     */
    DoctorResponse activate(UUID id);
    
    /**
     * Find doctors by specialty (case-insensitive)
     * @param specialty the specialty to filter by
     * @return list of doctor responses with matching specialty
     */
    List<DoctorResponse> findBySpecialtyIgnoreCase(String specialty);
    
    /**
     * Find active doctors by specialty (case-insensitive)
     * @param specialty the specialty to filter by
     * @return list of active doctor responses with matching specialty
     */
    List<DoctorResponse> findActiveBySpecialtyIgnoreCase(String specialty);
}
