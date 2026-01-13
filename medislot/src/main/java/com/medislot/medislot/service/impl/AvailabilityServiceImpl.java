package com.medislot.medislot.service.impl;

import com.medislot.medislot.dto.slot.SlotCreateRequest;
import com.medislot.medislot.dto.slot.SlotResponse;
import com.medislot.medislot.entity.Slot;
import com.medislot.medislot.entity.SlotStatus;
import com.medislot.medislot.exception.ResourceNotFoundException;
import com.medislot.medislot.mapper.SlotMapper;
import com.medislot.medislot.repository.DoctorRepository;
import com.medislot.medislot.repository.HospitalRepository;
import com.medislot.medislot.repository.SlotRepository;
import com.medislot.medislot.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of AvailabilityService with business logic
 */
@Service
@Transactional(readOnly = true)
public class AvailabilityServiceImpl implements AvailabilityService {
    
    private final SlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final SlotMapper slotMapper;
    
    @Autowired
    public AvailabilityServiceImpl(SlotRepository slotRepository,
                                    DoctorRepository doctorRepository,
                                    HospitalRepository hospitalRepository,
                                    SlotMapper slotMapper) {
        this.slotRepository = slotRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.slotMapper = slotMapper;
    }
    
    @Override
    @Transactional
    public SlotResponse create(SlotCreateRequest createRequest) {
        // Business Rule: Validate doctor exists
        if (!doctorRepository.existsById(createRequest.getDoctorId())) {
            throw new ResourceNotFoundException("Doctor", createRequest.getDoctorId());
        }
        
        // Business Rule: Validate hospital exists
        if (!hospitalRepository.existsById(createRequest.getHospitalId())) {
            throw new ResourceNotFoundException("Hospital", createRequest.getHospitalId());
        }
        
        // Business Rule: Cannot create overlapping slots for the same doctor
        UUID tempId = UUID.randomUUID(); // Temporary ID for new slot
        if (slotRepository.hasOverlappingSlot(
                createRequest.getDoctorId(),
                tempId,
                createRequest.getStartTime(),
                createRequest.getEndTime())) {
            throw new IllegalStateException(
                "Slot overlaps with an existing slot for this doctor");
        }
        
        Slot slot = slotMapper.toEntity(createRequest);
        Slot savedSlot = slotRepository.save(slot);
        return slotMapper.toResponse(savedSlot);
    }
    
    @Override
    public Optional<SlotResponse> findById(UUID id) {
        return slotRepository.findById(id)
                .map(slotMapper::toResponse);
    }
    
    @Override
    public List<SlotResponse> findAll() {
        return slotRepository.findAll().stream()
                .map(slotMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SlotResponse> findAvailableSlotsByDoctor(UUID doctorId) {
        return slotRepository.findByDoctorIdAndStatus(doctorId, SlotStatus.AVAILABLE).stream()
                .map(slotMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SlotResponse> findAvailableSlotsByHospital(UUID hospitalId) {
        return slotRepository.findByHospitalIdAndStatus(hospitalId, SlotStatus.AVAILABLE).stream()
                .map(slotMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SlotResponse> findAvailableSlotsByDoctorAndHospital(UUID doctorId, UUID hospitalId) {
        return slotRepository.findByDoctorIdAndHospitalIdAndStatus(doctorId, hospitalId, SlotStatus.AVAILABLE).stream()
                .map(slotMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SlotResponse> findSlotsByDoctorAndTimeRange(UUID doctorId, OffsetDateTime startTime, OffsetDateTime endTime) {
        return slotRepository.findByDoctorIdAndTimeRange(doctorId, startTime, endTime).stream()
                .map(slotMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SlotResponse update(UUID id, SlotCreateRequest updateRequest) {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));
        
        // Business Rule: Cannot update if slot is already booked
        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new IllegalStateException("Cannot update a booked slot");
        }
        
        // Update time if provided and check for overlaps
        if (updateRequest.getStartTime() != null && updateRequest.getEndTime() != null) {
            if (slotRepository.hasOverlappingSlot(
                    slot.getDoctorId(),
                    id,
                    updateRequest.getStartTime(),
                    updateRequest.getEndTime())) {
                throw new IllegalStateException(
                    "Slot overlaps with an existing slot for this doctor");
            }
            slot.setStartTime(updateRequest.getStartTime());
            slot.setEndTime(updateRequest.getEndTime());
        }
        
        if (updateRequest.getStatus() != null) {
            slot.setStatus(updateRequest.getStatus());
        }
        
        Slot updatedSlot = slotRepository.save(slot);
        return slotMapper.toResponse(updatedSlot);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));
        
        // Business Rule: Cannot delete if slot is booked
        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new IllegalStateException("Cannot delete a booked slot");
        }
        
        slotRepository.delete(slot);
    }
    
    @Override
    @Transactional
    public SlotResponse markAsBooked(UUID slotId) {
        return updateStatus(slotId, SlotStatus.BOOKED);
    }
    
    @Override
    @Transactional
    public SlotResponse markAsAvailable(UUID slotId) {
        return updateStatus(slotId, SlotStatus.AVAILABLE);
    }
    
    @Override
    @Transactional
    public SlotResponse updateStatus(UUID slotId, SlotStatus status) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", slotId));
        
        slot.setStatus(status);
        Slot updatedSlot = slotRepository.save(slot);
        return slotMapper.toResponse(updatedSlot);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return slotRepository.existsById(id);
    }
}
