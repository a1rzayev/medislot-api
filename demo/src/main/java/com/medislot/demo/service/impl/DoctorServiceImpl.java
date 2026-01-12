package com.medislot.demo.service.impl;

import com.medislot.demo.dto.doctor.DoctorCreateRequest;
import com.medislot.demo.dto.doctor.DoctorResponse;
import com.medislot.demo.dto.doctor.DoctorUpdateRequest;
import com.medislot.demo.entity.Doctor;
import com.medislot.demo.exception.DoctorDeletionNotAllowedException;
import com.medislot.demo.exception.ResourceNotFoundException;
import com.medislot.demo.mapper.DoctorMapper;
import com.medislot.demo.repository.DoctorRepository;
import com.medislot.demo.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of DoctorService with business logic
 */
@Service
@Transactional(readOnly = true)
public class DoctorServiceImpl implements DoctorService {
    
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    
    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }
    
    @Override
    @Transactional
    public DoctorResponse create(DoctorCreateRequest createRequest) {
        Doctor doctor = doctorMapper.toEntity(createRequest);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toResponse(savedDoctor);
    }
    
    @Override
    public Optional<DoctorResponse> findById(UUID id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toResponse);
    }
    
    @Override
    public List<DoctorResponse> findAll() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DoctorResponse> findAllActive() {
        return doctorRepository.findByActiveTrue().stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public DoctorResponse update(UUID id, DoctorUpdateRequest updateRequest) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        
        doctorMapper.updateEntity(doctor, updateRequest);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toResponse(updatedDoctor);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        
        // Business Rule: Cannot delete doctor with existing slots or appointments
        if (doctorRepository.hasSlots(id) || doctorRepository.hasAppointments(id)) {
            throw new DoctorDeletionNotAllowedException();
        }
        
        doctorRepository.delete(doctor);
    }
    
    @Override
    @Transactional
    public DoctorResponse deactivate(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        
        doctor.setActive(false);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toResponse(updatedDoctor);
    }
    
    @Override
    @Transactional
    public DoctorResponse activate(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        
        doctor.setActive(true);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toResponse(updatedDoctor);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return doctorRepository.existsById(id);
    }
    
    @Override
    public List<DoctorResponse> findBySpecialtyIgnoreCase(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty).stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DoctorResponse> findActiveBySpecialtyIgnoreCase(String specialty) {
        return doctorRepository.findByActiveTrueAndSpecialtyIgnoreCase(specialty).stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }
}
