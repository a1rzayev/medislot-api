package com.medislot.medislot.service;

import com.medislot.medislot.dto.appointment.AppointmentCreateRequest;
import com.medislot.medislot.dto.appointment.AppointmentResponse;
import com.medislot.medislot.entity.Appointment;
import com.medislot.medislot.entity.AppointmentStatus;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Appointment operations with business rules
 */
public interface AppointmentService extends BaseService<Appointment, UUID, AppointmentCreateRequest, AppointmentCreateRequest, AppointmentResponse> {
    
    /**
     * Find all appointments for a patient
     * @param patientId the patient ID
     * @return list of appointment responses
     */
    List<AppointmentResponse> findByPatientId(UUID patientId);
    
    /**
     * Find all appointments for a doctor
     * @param doctorId the doctor ID
     * @return list of appointment responses
     */
    List<AppointmentResponse> findByDoctorId(UUID doctorId);
    
    /**
     * Find all appointments at a hospital
     * @param hospitalId the hospital ID
     * @return list of appointment responses
     */
    List<AppointmentResponse> findByHospitalId(UUID hospitalId);
    
    /**
     * Cancel an appointment by patient
     * Business Rule: Patient can cancel before appointment time
     * @param appointmentId the appointment ID
     * @param patientId the patient ID (for authorization)
     * @return the cancelled appointment response
     */
    AppointmentResponse cancelByPatient(UUID appointmentId, UUID patientId);
    
    /**
     * Cancel an appointment by doctor
     * Business Rule: Doctor can only cancel appointments for same day
     * @param appointmentId the appointment ID
     * @param doctorId the doctor ID (for authorization)
     * @return the cancelled appointment response
     */
    AppointmentResponse cancelByDoctor(UUID appointmentId, UUID doctorId);
    
    /**
     * Update appointment status
     * @param appointmentId the appointment ID
     * @param status the new status
     * @return the updated appointment response
     */
    AppointmentResponse updateStatus(UUID appointmentId, AppointmentStatus status);
    
    /**
     * Find appointments by doctor and date
     * @param doctorId the doctor ID
     * @param date the appointment date
     * @return list of appointment responses
     */
    List<AppointmentResponse> findByDoctorIdAndDate(UUID doctorId, java.time.OffsetDateTime date);
    
    /**
     * Find appointments by multiple criteria
     * @param doctorId the doctor ID (optional)
     * @param patientId the patient ID (optional)
     * @param hospitalId the hospital ID (optional)
     * @param status the appointment status (optional)
     * @return list of appointment responses
     */
    List<AppointmentResponse> findByCriteria(UUID doctorId, UUID patientId, UUID hospitalId, AppointmentStatus status);
}
