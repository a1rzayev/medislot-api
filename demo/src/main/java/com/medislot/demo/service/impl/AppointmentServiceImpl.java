package com.medislot.demo.service.impl;

import com.medislot.demo.dto.appointment.AppointmentCreateRequest;
import com.medislot.demo.dto.appointment.AppointmentResponse;
import com.medislot.demo.entity.Appointment;
import com.medislot.demo.entity.AppointmentStatus;
import com.medislot.demo.entity.Slot;
import com.medislot.demo.entity.SlotStatus;
import com.medislot.demo.exception.ResourceNotFoundException;
import com.medislot.demo.mapper.AppointmentMapper;
import com.medislot.demo.repository.AppointmentRepository;
import com.medislot.demo.repository.DoctorRepository;
import com.medislot.demo.repository.HospitalRepository;
import com.medislot.demo.repository.PatientRepository;
import com.medislot.demo.repository.SlotRepository;
import com.medislot.demo.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of AppointmentService with core business rules
 */
@Service
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final SlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;
    
    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                   SlotRepository slotRepository,
                                   DoctorRepository doctorRepository,
                                   HospitalRepository hospitalRepository,
                                   PatientRepository patientRepository,
                                   AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.slotRepository = slotRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.patientRepository = patientRepository;
        this.appointmentMapper = appointmentMapper;
    }
    
    @Override
    @Transactional
    public AppointmentResponse create(AppointmentCreateRequest createRequest) {
        // Validate entities exist
        if (!doctorRepository.existsById(createRequest.getDoctorId())) {
            throw new ResourceNotFoundException("Doctor", createRequest.getDoctorId());
        }
        if (!hospitalRepository.existsById(createRequest.getHospitalId())) {
            throw new ResourceNotFoundException("Hospital", createRequest.getHospitalId());
        }
        if (!patientRepository.existsById(createRequest.getPatientId())) {
            throw new ResourceNotFoundException("Patient", createRequest.getPatientId());
        }
        
        // Business Rule: Appointment must be inside doctor slot
        Slot slot = slotRepository.findById(createRequest.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot", createRequest.getSlotId()));
        
        // Validate slot belongs to the specified doctor and hospital
        if (!slot.getDoctorId().equals(createRequest.getDoctorId())) {
            throw new IllegalStateException("Slot does not belong to the specified doctor");
        }
        if (!slot.getHospitalId().equals(createRequest.getHospitalId())) {
            throw new IllegalStateException("Slot does not belong to the specified hospital");
        }
        
        // Business Rule: Cannot double-book the same slot
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Slot is not available for booking");
        }
        
        // Check if slot already has an appointment
        Optional<Appointment> existingAppointment = appointmentRepository.findBySlotId(createRequest.getSlotId());
        if (existingAppointment.isPresent()) {
            throw new IllegalStateException("Slot is already booked");
        }
        
        // Business Rule: Cannot double-book the same doctor/time
        if (appointmentRepository.isTimeSlotBooked(
                createRequest.getDoctorId(),
                slot.getStartTime(),
                slot.getEndTime())) {
            throw new IllegalStateException("Doctor is already booked for this time slot");
        }
        
        // Create appointment
        Appointment appointment = appointmentMapper.toEntity(createRequest);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Mark slot as booked
        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);
        
        return appointmentMapper.toResponse(savedAppointment);
    }
    
    @Override
    public Optional<AppointmentResponse> findById(UUID id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toResponse);
    }
    
    @Override
    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AppointmentResponse> findByPatientId(UUID patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AppointmentResponse> findByDoctorId(UUID doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AppointmentResponse> findByHospitalId(UUID hospitalId) {
        return appointmentRepository.findByHospitalId(hospitalId).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public AppointmentResponse update(UUID id, AppointmentCreateRequest updateRequest) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        
        // Business Rule: Cannot update a cancelled appointment
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update a cancelled appointment");
        }
        
        // For now, only allow status updates
        if (updateRequest.getStatus() != null) {
            appointment.setStatus(updateRequest.getStatus());
        }
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(updatedAppointment);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        
        // Release the slot if appointment is being deleted
        if (appointment.getStatus() == AppointmentStatus.BOOKED) {
            Slot slot = slotRepository.findById(appointment.getSlotId())
                    .orElse(null);
            if (slot != null) {
                slot.setStatus(SlotStatus.AVAILABLE);
                slotRepository.save(slot);
            }
        }
        
        appointmentRepository.delete(appointment);
    }
    
    @Override
    @Transactional
    public AppointmentResponse cancelByPatient(UUID appointmentId, UUID patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        
        // Verify the appointment belongs to the patient
        if (!appointment.getPatientId().equals(patientId)) {
            throw new IllegalStateException("Appointment does not belong to this patient");
        }
        
        // Business Rule: Patient can cancel before appointment time
        Slot slot = slotRepository.findById(appointment.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot", appointment.getSlotId()));
        
        if (slot.getStartTime().isBefore(OffsetDateTime.now())) {
            throw new IllegalStateException("Cannot cancel appointment that has already started or passed");
        }
        
        // Cancel the appointment
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);
        
        // Mark slot as available again
        slot.setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(slot);
        
        return appointmentMapper.toResponse(cancelledAppointment);
    }
    
    @Override
    @Transactional
    public AppointmentResponse cancelByDoctor(UUID appointmentId, UUID doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        
        // Verify the appointment belongs to the doctor
        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new IllegalStateException("Appointment does not belong to this doctor");
        }
        
        // Business Rule: Doctor can only modify appointments for same day
        Slot slot = slotRepository.findById(appointment.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot", appointment.getSlotId()));
        
        LocalDate appointmentDate = slot.getStartTime().toLocalDate();
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        
        if (!appointmentDate.equals(today)) {
            throw new IllegalStateException("Doctor can only cancel appointments for the same day");
        }
        
        // Cancel the appointment
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);
        
        // Mark slot as available again
        slot.setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(slot);
        
        return appointmentMapper.toResponse(cancelledAppointment);
    }
    
    @Override
    @Transactional
    public AppointmentResponse updateStatus(UUID appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        
        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        // Update slot status if cancelling
        if (status == AppointmentStatus.CANCELLED && oldStatus == AppointmentStatus.BOOKED) {
            Slot slot = slotRepository.findById(appointment.getSlotId())
                    .orElse(null);
            if (slot != null) {
                slot.setStatus(SlotStatus.AVAILABLE);
                slotRepository.save(slot);
            }
        }
        
        return appointmentMapper.toResponse(updatedAppointment);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return appointmentRepository.existsById(id);
    }
}
