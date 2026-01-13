package com.medislot.demo.controller;

import com.medislot.demo.dto.ApiResponse;
import com.medislot.demo.dto.doctor.DoctorCreateRequest;
import com.medislot.demo.dto.doctor.DoctorResponse;
import com.medislot.demo.dto.doctor.DoctorUpdateRequest;
import com.medislot.demo.exception.ResourceNotFoundException;
import com.medislot.demo.service.DoctorService;
import com.medislot.demo.util.ResponseHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Doctor operations
 * Handles CRUD operations, filtering, and pagination
 */
@RestController
@RequestMapping("/api/doctors")
@Tag(name = "Doctors", description = "Doctor management APIs - Create, read, update, and manage doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * Create a new doctor
     * POST /api/doctors
     * Access: ADMIN only
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new doctor", description = "Creates a new doctor in the system (ADMIN only)")
    public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(
            @Valid @RequestBody DoctorCreateRequest request) {
        DoctorResponse doctor = doctorService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseHelper.success(doctor, "Doctor created successfully"));
    }

    /**
     * Get all doctors with optional filtering by specialization (case-insensitive)
     * GET /api/doctors?specialization=Cardiology
     * Supports pagination: /api/doctors?page=0&size=10&sort=fullName,asc
     */
    @GetMapping
    @Operation(summary = "Get all doctors", description = "Retrieves all doctors with optional filtering by specialization (case-insensitive) and active status")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAllDoctors(
            @Parameter(description = "Filter by specialty (case-insensitive)") @RequestParam(required = false) String specialization,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean active) {
        
        List<DoctorResponse> doctors;
        
        // Filter by both specialization and active status
        if (specialization != null && !specialization.isEmpty()) {
            if (active != null && active) {
                doctors = doctorService.findActiveBySpecialtyIgnoreCase(specialization);
            } else {
                doctors = doctorService.findBySpecialtyIgnoreCase(specialization);
            }
        } else if (active != null && active) {
            doctors = doctorService.findAllActive();
        } else {
            doctors = doctorService.findAll();
        }
        
        return ResponseEntity.ok(
                ResponseHelper.success(doctors, "Doctors retrieved successfully"));
    }

    /**
     * Get active doctors only
     * GET /api/doctors/active
     * NOTE: This must be defined BEFORE /{id} to avoid path matching conflicts
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getActiveDoctors() {
        List<DoctorResponse> doctors = doctorService.findAllActive();
        return ResponseEntity.ok(
                ResponseHelper.success(doctors, "Active doctors retrieved successfully"));
    }

    /**
     * Get doctor by ID
     * GET /api/doctors/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Retrieves a specific doctor by their unique identifier")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(
            @Parameter(description = "Doctor UUID") @PathVariable UUID id) {
        DoctorResponse doctor = doctorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        return ResponseEntity.ok(
                ResponseHelper.success(doctor, "Doctor retrieved successfully"));
    }

    /**
     * Update a doctor
     * PUT /api/doctors/{id}
     * Access: ADMIN only
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
            @PathVariable UUID id,
            @Valid @RequestBody DoctorUpdateRequest request) {
        DoctorResponse doctor = doctorService.update(id, request);
        return ResponseEntity.ok(
                ResponseHelper.success(doctor, "Doctor updated successfully"));
    }

    /**
     * Delete a doctor (soft delete - deactivates the doctor)
     * DELETE /api/doctors/{id}
     * Access: ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable UUID id) {
        doctorService.delete(id);
        return ResponseEntity.ok(
                ResponseHelper.success(null, "Doctor deleted successfully"));
    }

    /**
     * Deactivate a doctor
     * POST /api/doctors/{id}/deactivate
     * Access: ADMIN only
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> deactivateDoctor(@PathVariable UUID id) {
        DoctorResponse doctor = doctorService.deactivate(id);
        return ResponseEntity.ok(
                ResponseHelper.success(doctor, "Doctor deactivated successfully"));
    }

    /**
     * Activate a doctor
     * POST /api/doctors/{id}/activate
     * Access: ADMIN only
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> activateDoctor(@PathVariable UUID id) {
        DoctorResponse doctor = doctorService.activate(id);
        return ResponseEntity.ok(
                ResponseHelper.success(doctor, "Doctor activated successfully"));
    }
}
