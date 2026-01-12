package com.medislot.demo.controller;

import com.medislot.demo.dto.ApiResponse;
import com.medislot.demo.dto.doctor.DoctorCreateRequest;
import com.medislot.demo.dto.doctor.DoctorResponse;
import com.medislot.demo.dto.doctor.DoctorUpdateRequest;
import com.medislot.demo.exception.ResourceNotFoundException;
import com.medislot.demo.service.DoctorService;
import com.medislot.demo.util.ResponseHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Doctor operations
 * Handles CRUD operations, filtering, and pagination
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * Create a new doctor
     * POST /api/doctors
     */
    @PostMapping
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
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAllDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Boolean active) {
        
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
     * Get doctor by ID
     * GET /api/doctors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(@PathVariable UUID id) {
        DoctorResponse doctor = doctorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        return ResponseEntity.ok(
                ResponseHelper.success(doctor, "Doctor retrieved successfully"));
    }

    /**
     * Update a doctor
     * PUT /api/doctors/{id}
     */
    @PutMapping("/{id}")
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
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable UUID id) {
        doctorService.delete(id);
        return ResponseEntity.ok(
                ResponseHelper.success(null, "Doctor deleted successfully"));
    }

    /**
     * Deactivate a doctor
     * POST /api/doctors/{id}/deactivate
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<DoctorResponse>> deactivateDoctor(@PathVariable UUID id) {
        DoctorResponse doctor = doctorService.deactivate(id);
        return ResponseEntity.ok(
                ResponseHelper.success(doctor, "Doctor deactivated successfully"));
    }

    /**
     * Activate a doctor
     * POST /api/doctors/{id}/activate
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<DoctorResponse>> activateDoctor(@PathVariable UUID id) {
        DoctorResponse doctor = doctorService.activate(id);
        return ResponseEntity.ok(
                ResponseHelper.success(doctor, "Doctor activated successfully"));
    }

    /**
     * Get active doctors only
     * GET /api/doctors/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getActiveDoctors() {
        List<DoctorResponse> doctors = doctorService.findAllActive();
        return ResponseEntity.ok(
                ResponseHelper.success(doctors, "Active doctors retrieved successfully"));
    }
}
