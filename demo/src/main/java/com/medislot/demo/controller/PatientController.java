package com.medislot.demo.controller;

import com.medislot.demo.dto.ApiResponse;
import com.medislot.demo.dto.patient.PatientCreateRequest;
import com.medislot.demo.dto.patient.PatientResponse;
import com.medislot.demo.exception.ResourceNotFoundException;
import com.medislot.demo.service.PatientService;
import com.medislot.demo.util.ResponseHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Patient operations
 * Handles CRUD operations (create, read, update - no delete)
 */
@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patients", description = "Patient management APIs - Create, read, update, and manage patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Create a new patient
     * POST /api/patients
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(
            @Valid @RequestBody PatientCreateRequest request) {
        PatientResponse patient = patientService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseHelper.success(patient, "Patient created successfully"));
    }

    /**
     * Get all patients
     * GET /api/patients
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients(
            @RequestParam(required = false) Boolean active) {
        
        List<PatientResponse> patients;
        
        if (active != null && active) {
            patients = patientService.findAllActive();
        } else {
            patients = patientService.findAll();
        }
        
        return ResponseEntity.ok(
                ResponseHelper.success(patients, "Patients retrieved successfully"));
    }

    /**
     * Get active patients only
     * GET /api/patients/active
     * NOTE: This must be defined BEFORE /{id} to avoid path matching conflicts
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getActivePatients() {
        List<PatientResponse> patients = patientService.findAllActive();
        return ResponseEntity.ok(
                ResponseHelper.success(patients, "Active patients retrieved successfully"));
    }

    /**
     * Get patient by ID
     * GET /api/patients/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(@PathVariable UUID id) {
        PatientResponse patient = patientService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        return ResponseEntity.ok(
                ResponseHelper.success(patient, "Patient retrieved successfully"));
    }

    /**
     * Update a patient
     * PUT /api/patients/{id}
     * Access: ADMIN only (or patient themselves for future enhancement)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientCreateRequest request) {
        PatientResponse patient = patientService.update(id, request);
        return ResponseEntity.ok(
                ResponseHelper.success(patient, "Patient updated successfully"));
    }

    /**
     * Deactivate a patient (soft delete)
     * POST /api/patients/{id}/deactivate
     * Access: ADMIN only
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PatientResponse>> deactivatePatient(@PathVariable UUID id) {
        PatientResponse patient = patientService.deactivate(id);
        return ResponseEntity.ok(
                ResponseHelper.success(patient, "Patient deactivated successfully"));
    }

    /**
     * Activate a patient
     * POST /api/patients/{id}/activate
     * Access: ADMIN only
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PatientResponse>> activatePatient(@PathVariable UUID id) {
        PatientResponse patient = patientService.activate(id);
        return ResponseEntity.ok(
                ResponseHelper.success(patient, "Patient activated successfully"));
    }
}
