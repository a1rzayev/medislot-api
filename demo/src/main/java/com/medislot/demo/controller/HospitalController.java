package com.medislot.demo.controller;

import com.medislot.demo.dto.ApiResponse;
import com.medislot.demo.dto.hospital.HospitalCreateRequest;
import com.medislot.demo.dto.hospital.HospitalResponse;
import com.medislot.demo.exception.ResourceNotFoundException;
import com.medislot.demo.service.HospitalService;
import com.medislot.demo.util.ResponseHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Hospital operations
 * Handles create, read, and get operations (no update/delete per requirements)
 */
@RestController
@RequestMapping("/api/hospitals")
@Tag(name = "Hospitals", description = "Hospital management APIs - Create, read, and manage hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    /**
     * Create a new hospital
     * POST /api/hospitals
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HospitalResponse>> createHospital(
            @Valid @RequestBody HospitalCreateRequest request) {
        HospitalResponse hospital = hospitalService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseHelper.success(hospital, "Hospital created successfully"));
    }

    /**
     * Get all hospitals
     * GET /api/hospitals
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HospitalResponse>>> getAllHospitals(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String city) {
        
        List<HospitalResponse> hospitals;
        
        if (active != null && active) {
            hospitals = hospitalService.findAllActive();
        } else {
            hospitals = hospitalService.findAll();
        }
        
        // Filter by city if provided
        if (city != null && !city.isEmpty()) {
            hospitals = hospitals.stream()
                    .filter(h -> h.getAddress() != null && 
                                 h.getAddress().toLowerCase().contains(city.toLowerCase()))
                    .toList();
        }
        
        return ResponseEntity.ok(
                ResponseHelper.success(hospitals, "Hospitals retrieved successfully"));
    }

    /**
     * Get hospital by ID
     * GET /api/hospitals/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalResponse>> getHospitalById(@PathVariable UUID id) {
        HospitalResponse hospital = hospitalService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital", id));
        return ResponseEntity.ok(
                ResponseHelper.success(hospital, "Hospital retrieved successfully"));
    }

    /**
     * Get active hospitals only
     * GET /api/hospitals/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<HospitalResponse>>> getActiveHospitals() {
        List<HospitalResponse> hospitals = hospitalService.findAllActive();
        return ResponseEntity.ok(
                ResponseHelper.success(hospitals, "Active hospitals retrieved successfully"));
    }

    /**
     * Deactivate a hospital (soft delete)
     * POST /api/hospitals/{id}/deactivate
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<HospitalResponse>> deactivateHospital(@PathVariable UUID id) {
        HospitalResponse hospital = hospitalService.deactivate(id);
        return ResponseEntity.ok(
                ResponseHelper.success(hospital, "Hospital deactivated successfully"));
    }

    /**
     * Activate a hospital
     * POST /api/hospitals/{id}/activate
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<HospitalResponse>> activateHospital(@PathVariable UUID id) {
        HospitalResponse hospital = hospitalService.activate(id);
        return ResponseEntity.ok(
                ResponseHelper.success(hospital, "Hospital activated successfully"));
    }
}
