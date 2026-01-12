package com.medislot.demo.service;

import com.medislot.demo.dto.slot.SlotCreateRequest;
import com.medislot.demo.dto.slot.SlotResponse;
import com.medislot.demo.entity.Slot;
import com.medislot.demo.entity.SlotStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Availability/Slot operations
 */
public interface AvailabilityService extends BaseService<Slot, UUID, SlotCreateRequest, SlotCreateRequest, SlotResponse> {
    
    /**
     * Find available slots by doctor
     * @param doctorId the doctor ID
     * @return list of available slot responses
     */
    List<SlotResponse> findAvailableSlotsByDoctor(UUID doctorId);
    
    /**
     * Find available slots by hospital
     * @param hospitalId the hospital ID
     * @return list of available slot responses
     */
    List<SlotResponse> findAvailableSlotsByHospital(UUID hospitalId);
    
    /**
     * Find available slots by doctor and hospital
     * @param doctorId the doctor ID
     * @param hospitalId the hospital ID
     * @return list of available slot responses
     */
    List<SlotResponse> findAvailableSlotsByDoctorAndHospital(UUID doctorId, UUID hospitalId);
    
    /**
     * Find slots by doctor in a time range
     * @param doctorId the doctor ID
     * @param startTime start of time range
     * @param endTime end of time range
     * @return list of slot responses
     */
    List<SlotResponse> findSlotsByDoctorAndTimeRange(UUID doctorId, OffsetDateTime startTime, OffsetDateTime endTime);
    
    /**
     * Mark a slot as booked
     * @param slotId the slot ID
     * @return the updated slot response
     */
    SlotResponse markAsBooked(UUID slotId);
    
    /**
     * Mark a slot as available
     * @param slotId the slot ID
     * @return the updated slot response
     */
    SlotResponse markAsAvailable(UUID slotId);
    
    /**
     * Update slot status
     * @param slotId the slot ID
     * @param status the new status
     * @return the updated slot response
     */
    SlotResponse updateStatus(UUID slotId, SlotStatus status);
}
