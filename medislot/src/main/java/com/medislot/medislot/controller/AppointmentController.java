package com.medislot.medislot.controller;

import com.medislot.medislot.dto.ApiResponse;
import com.medislot.medislot.dto.appointment.AppointmentCreateRequest;
import com.medislot.medislot.dto.appointment.AppointmentResponse;
import com.medislot.medislot.entity.AppointmentStatus;
import com.medislot.medislot.exception.ResourceNotFoundException;
import com.medislot.medislot.service.AppointmentService;
import com.medislot.medislot.util.ResponseHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Appointment operations
 * Handles appointment booking, approval, cancellation, and rescheduling
 */
@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Appointment management APIs - Book, approve, cancel, and reschedule appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * Create a new appointment (Patient creates appointment request)
     * POST /api/appointments
     * Business Rule: Patient can request appointments for available slots
     * Access: PATIENT (or ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @Operation(summary = "Create appointment", description = "Patient creates a new appointment request for an available slot")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @Valid @RequestBody AppointmentCreateRequest request) {
        AppointmentResponse appointment = appointmentService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseHelper.success(appointment, "Appointment created successfully"));
    }

    /**
     * Get all appointments with optional filtering
     * GET /api/appointments?doctorId=...&patientId=...&hospitalId=...&status=...&date=...
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments(
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AppointmentResponse> appointments;
        
        // Use the date-specific query if date is provided
        if (doctorId != null && date != null) {
            OffsetDateTime dateTime = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            appointments = appointmentService.findByDoctorIdAndDate(doctorId, dateTime);
        } 
        // Use the multi-criteria query when multiple filters are provided
        else if ((doctorId != null || patientId != null || hospitalId != null) && status != null) {
            appointments = appointmentService.findByCriteria(doctorId, patientId, hospitalId, status);
        }
        // Priority filtering by specific criteria
        else if (patientId != null) {
            appointments = appointmentService.findByPatientId(patientId);
        } else if (doctorId != null) {
            appointments = appointmentService.findByDoctorId(doctorId);
        } else if (hospitalId != null) {
            appointments = appointmentService.findByHospitalId(hospitalId);
        } else {
            appointments = appointmentService.findAll();
        }
        
        // Apply status filter if not already applied and provided
        if (status != null && (doctorId == null && patientId == null && hospitalId == null)) {
            appointments = appointments.stream()
                    .filter(a -> a.getStatus() == status)
                    .toList();
        }
        
        return ResponseEntity.ok(
                ResponseHelper.success(appointments, "Appointments retrieved successfully"));
    }

    /**
     * Get appointments by patient ID
     * GET /api/appointments/patient/{patientId}
     * NOTE: Specific paths like /patient/, /doctor/, /hospital/ must be defined BEFORE /{id}
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByPatient(
            @PathVariable UUID patientId) {
        List<AppointmentResponse> appointments = appointmentService.findByPatientId(patientId);
        return ResponseEntity.ok(
                ResponseHelper.success(appointments, "Patient appointments retrieved successfully"));
    }

    /**
     * Get appointments by doctor ID
     * GET /api/appointments/doctor/{doctorId}?date=2024-01-15
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AppointmentResponse> appointments;
        
        if (date != null) {
            OffsetDateTime dateTime = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            appointments = appointmentService.findByDoctorIdAndDate(doctorId, dateTime);
        } else {
            appointments = appointmentService.findByDoctorId(doctorId);
        }
        
        return ResponseEntity.ok(
                ResponseHelper.success(appointments, "Doctor appointments retrieved successfully"));
    }

    /**
     * Get appointments by hospital ID
     * GET /api/appointments/hospital/{hospitalId}
     */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByHospital(
            @PathVariable UUID hospitalId) {
        List<AppointmentResponse> appointments = appointmentService.findByHospitalId(hospitalId);
        return ResponseEntity.ok(
                ResponseHelper.success(appointments, "Hospital appointments retrieved successfully"));
    }

    /**
     * Get appointment by ID
     * GET /api/appointments/{id}
     * NOTE: This generic /{id} endpoint must be defined AFTER more specific paths
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(@PathVariable UUID id) {
        AppointmentResponse appointment = appointmentService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        return ResponseEntity.ok(
                ResponseHelper.success(appointment, "Appointment retrieved successfully"));
    }

    /**
     * Approve an appointment (Doctor approves appointment request)
     * POST /api/appointments/{id}/approve
     * Business Rule: Only doctor can approve their appointments
     * Access: DOCTOR or ADMIN
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> approveAppointment(
            @PathVariable UUID id,
            @RequestParam UUID doctorId) {
        AppointmentResponse appointment = appointmentService.updateStatus(id, AppointmentStatus.BOOKED);
        return ResponseEntity.ok(
                ResponseHelper.success(appointment, "Appointment approved successfully"));
    }

    /**
     * Deny an appointment (Doctor denies appointment request)
     * POST /api/appointments/{id}/deny
     * Business Rule: Only doctor can deny their appointments
     * Access: DOCTOR or ADMIN
     */
    @PostMapping("/{id}/deny")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> denyAppointment(
            @PathVariable UUID id,
            @RequestParam UUID doctorId) {
        AppointmentResponse appointment = appointmentService.updateStatus(id, AppointmentStatus.CANCELLED);
        return ResponseEntity.ok(
                ResponseHelper.success(appointment, "Appointment denied successfully"));
    }

    /**
     * Cancel an appointment by patient
     * POST /api/appointments/{id}/cancel/patient
     * Business Rule: Patient can cancel before appointment time
     * Access: PATIENT or ADMIN
     */
    @PostMapping("/{id}/cancel/patient")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @Operation(summary = "Cancel appointment (Patient)", description = "Patient cancels their appointment before the scheduled time")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointmentByPatient(
            @Parameter(description = "Appointment ID") @PathVariable UUID id,
            @Parameter(description = "Patient ID for authorization") @RequestParam UUID patientId) {
        AppointmentResponse appointment = appointmentService.cancelByPatient(id, patientId);
        return ResponseEntity.ok(
                ResponseHelper.success(appointment, "Appointment cancelled by patient successfully"));
    }

    /**
     * Cancel an appointment by doctor
     * POST /api/appointments/{id}/cancel/doctor
     * Business Rule: Doctor can only cancel appointments for same day
     * Access: DOCTOR or ADMIN
     */
    @PostMapping("/{id}/cancel/doctor")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Cancel appointment (Doctor)", description = "Doctor cancels appointment (same day only)")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointmentByDoctor(
            @Parameter(description = "Appointment ID") @PathVariable UUID id,
            @Parameter(description = "Doctor ID for authorization") @RequestParam UUID doctorId) {
        AppointmentResponse appointment = appointmentService.cancelByDoctor(id, doctorId);
        return ResponseEntity.ok(
                ResponseHelper.success(appointment, "Appointment cancelled by doctor successfully"));
    }

    /**
     * Reschedule an appointment (same day)
     * POST /api/appointments/{id}/reschedule
     * Business Rule: Doctor can reschedule to another slot on same day
     * Access: DOCTOR or ADMIN
     */
    @PostMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> rescheduleAppointment(
            @PathVariable UUID id,
            @RequestParam UUID doctorId,
            @RequestParam UUID newSlotId) {
        // First, cancel the current appointment by doctor
        appointmentService.cancelByDoctor(id, doctorId);
        
        // Create a new appointment with the new slot
        // Note: This is a simplified approach. In production, you might want a dedicated reschedule method
        // that validates the doctor, checks same day, and handles slot transitions atomically
        AppointmentResponse currentAppointment = appointmentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        AppointmentCreateRequest newRequest = new AppointmentCreateRequest();
        newRequest.setSlotId(newSlotId);
        newRequest.setPatientId(currentAppointment.getPatientId());
        newRequest.setDoctorId(currentAppointment.getDoctorId());
        newRequest.setHospitalId(currentAppointment.getHospitalId());
        
        AppointmentResponse newAppointment = appointmentService.create(newRequest);
        
        return ResponseEntity.ok(
                ResponseHelper.success(newAppointment, "Appointment rescheduled successfully"));
    }

    /**
     * Update appointment status
     * PATCH /api/appointments/{id}/status
     * Access: DOCTOR or ADMIN
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointmentStatus(
            @PathVariable UUID id,
            @RequestParam AppointmentStatus status) {
        AppointmentResponse appointment = appointmentService.updateStatus(id, status);
        return ResponseEntity.ok(
                ResponseHelper.success(appointment, "Appointment status updated successfully"));
    }

    /**
     * Delete an appointment
     * DELETE /api/appointments/{id}
     * Access: ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable UUID id) {
        appointmentService.delete(id);
        return ResponseEntity.ok(
                ResponseHelper.success(null, "Appointment deleted successfully"));
    }
}
