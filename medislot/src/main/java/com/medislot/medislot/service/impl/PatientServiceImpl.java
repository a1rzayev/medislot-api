package com.medislot.medislot.service.impl;

import com.medislot.medislot.dto.patient.PatientCreateRequest;
import com.medislot.medislot.dto.patient.PatientResponse;
import com.medislot.medislot.entity.Patient;
import com.medislot.medislot.exception.ResourceNotFoundException;
import com.medislot.medislot.mapper.PatientMapper;
import com.medislot.medislot.repository.PatientRepository;
import com.medislot.medislot.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PatientService with business logic
 */
@Service
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {
    
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    
    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }
    
    @Override
    @Transactional
    public PatientResponse create(PatientCreateRequest createRequest) {
        Patient patient = patientMapper.toEntity(createRequest);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(savedPatient);
    }
    
    @Override
    public Optional<PatientResponse> findById(UUID id) {
        return patientRepository.findById(id)
                .map(patientMapper::toResponse);
    }
    
    @Override
    public List<PatientResponse> findAll() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PatientResponse> findAllActive() {
        return patientRepository.findAll().stream()
                .filter(Patient::getActive)
                .map(patientMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PatientResponse update(UUID id, PatientCreateRequest updateRequest) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        
        // Update fields
        if (updateRequest.getFullName() != null) {
            patient.setFullName(updateRequest.getFullName());
        }
        if (updateRequest.getPhone() != null) {
            patient.setPhone(updateRequest.getPhone());
        }
        if (updateRequest.getEmail() != null) {
            patient.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getActive() != null) {
            patient.setActive(updateRequest.getActive());
        }
        
        Patient updatedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(updatedPatient);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        
        patientRepository.delete(patient);
    }
    
    @Override
    @Transactional
    public PatientResponse deactivate(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        
        patient.setActive(false);
        Patient updatedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(updatedPatient);
    }
    
    @Override
    @Transactional
    public PatientResponse activate(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        
        patient.setActive(true);
        Patient updatedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(updatedPatient);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return patientRepository.existsById(id);
    }
}
