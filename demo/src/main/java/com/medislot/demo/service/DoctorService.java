package com.medislot.demo.service;

import com.medislot.demo.dto.doctor.DoctorCreateRequest;
import com.medislot.demo.dto.doctor.DoctorResponse;
import com.medislot.demo.dto.doctor.DoctorUpdateRequest;
import com.medislot.demo.entity.Doctor;

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
}
