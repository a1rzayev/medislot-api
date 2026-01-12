package com.medislot.demo.service.impl;

import com.medislot.demo.dto.hospital.HospitalCreateRequest;
import com.medislot.demo.dto.hospital.HospitalResponse;
import com.medislot.demo.entity.Hospital;
import com.medislot.demo.exception.ResourceNotFoundException;
import com.medislot.demo.mapper.HospitalMapper;
import com.medislot.demo.repository.HospitalRepository;
import com.medislot.demo.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of HospitalService with business logic
 */
@Service
@Transactional(readOnly = true)
public class HospitalServiceImpl implements HospitalService {
    
    private final HospitalRepository hospitalRepository;
    private final HospitalMapper hospitalMapper;
    
    @Autowired
    public HospitalServiceImpl(HospitalRepository hospitalRepository, HospitalMapper hospitalMapper) {
        this.hospitalRepository = hospitalRepository;
        this.hospitalMapper = hospitalMapper;
    }
    
    @Override
    @Transactional
    public HospitalResponse create(HospitalCreateRequest createRequest) {
        Hospital hospital = hospitalMapper.toEntity(createRequest);
        Hospital savedHospital = hospitalRepository.save(hospital);
        return hospitalMapper.toResponse(savedHospital);
    }
    
    @Override
    public Optional<HospitalResponse> findById(UUID id) {
        return hospitalRepository.findById(id)
                .map(hospitalMapper::toResponse);
    }
    
    @Override
    public List<HospitalResponse> findAll() {
        return hospitalRepository.findAll().stream()
                .map(hospitalMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<HospitalResponse> findAllActive() {
        return hospitalRepository.findByActiveTrue().stream()
                .map(hospitalMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public HospitalResponse update(UUID id, HospitalCreateRequest updateRequest) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital", id));
        
        // Update fields
        if (updateRequest.getName() != null) {
            hospital.setName(updateRequest.getName());
        }
        if (updateRequest.getAddress() != null) {
            hospital.setAddress(updateRequest.getAddress());
        }
        if (updateRequest.getActive() != null) {
            hospital.setActive(updateRequest.getActive());
        }
        
        Hospital updatedHospital = hospitalRepository.save(hospital);
        return hospitalMapper.toResponse(updatedHospital);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital", id));
        
        hospitalRepository.delete(hospital);
    }
    
    @Override
    @Transactional
    public HospitalResponse deactivate(UUID id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital", id));
        
        hospital.setActive(false);
        Hospital updatedHospital = hospitalRepository.save(hospital);
        return hospitalMapper.toResponse(updatedHospital);
    }
    
    @Override
    @Transactional
    public HospitalResponse activate(UUID id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital", id));
        
        hospital.setActive(true);
        Hospital updatedHospital = hospitalRepository.save(hospital);
        return hospitalMapper.toResponse(updatedHospital);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return hospitalRepository.existsById(id);
    }
}
