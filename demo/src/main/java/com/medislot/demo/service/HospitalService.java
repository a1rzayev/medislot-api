package com.medislot.demo.service;

import com.medislot.demo.dto.hospital.HospitalCreateRequest;
import com.medislot.demo.dto.hospital.HospitalResponse;
import com.medislot.demo.entity.Hospital;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Hospital operations
 */
public interface HospitalService extends BaseService<Hospital, UUID, HospitalCreateRequest, HospitalCreateRequest, HospitalResponse> {
    
    /**
     * Find all active hospitals
     * @return list of active hospital responses
     */
    List<HospitalResponse> findAllActive();
    
    /**
     * Deactivate a hospital (soft delete)
     * @param id the hospital ID
     * @return the deactivated hospital response
     */
    HospitalResponse deactivate(UUID id);
    
    /**
     * Activate a hospital
     * @param id the hospital ID
     * @return the activated hospital response
     */
    HospitalResponse activate(UUID id);
}
