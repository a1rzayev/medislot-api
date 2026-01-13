package com.medislot.medislot.controller;

import com.medislot.medislot.dto.ApiResponse;
import com.medislot.medislot.dto.slot.SlotCreateRequest;
import com.medislot.medislot.dto.slot.SlotResponse;
import com.medislot.medislot.entity.SlotStatus;
import com.medislot.medislot.exception.ResourceNotFoundException;
import com.medislot.medislot.service.AvailabilityService;
import com.medislot.medislot.util.ResponseHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Availability/Slot operations
 * Manages doctor availability slots
 */
@RestController
@RequestMapping("/api/availability")
@Tag(name = "Availability", description = "Slot/Availability management APIs - Manage doctor availability slots and schedules")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    /**
     * Create a new availability slot
     * POST /api/availability
     * Access: ADMIN or DOCTOR (for their own slots)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<SlotResponse>> createSlot(
            @Valid @RequestBody SlotCreateRequest request) {
        SlotResponse slot = availabilityService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseHelper.success(slot, "Availability slot created successfully"));
    }

    /**
     * Get all slots
     * GET /api/availability
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getAllSlots() {
        List<SlotResponse> slots = availabilityService.findAll();
        return ResponseEntity.ok(
                ResponseHelper.success(slots, "Slots retrieved successfully"));
    }

    /**
     * Get slot by ID
     * GET /api/availability/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SlotResponse>> getSlotById(@PathVariable UUID id) {
        SlotResponse slot = availabilityService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));
        return ResponseEntity.ok(
                ResponseHelper.success(slot, "Slot retrieved successfully"));
    }

    /**
     * Get available slots by doctor
     * GET /api/availability/doctor/{doctorId}/available
     */
    @GetMapping("/doctor/{doctorId}/available")
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getAvailableSlotsByDoctor(
            @PathVariable UUID doctorId) {
        List<SlotResponse> slots = availabilityService.findAvailableSlotsByDoctor(doctorId);
        return ResponseEntity.ok(
                ResponseHelper.success(slots, "Available slots for doctor retrieved successfully"));
    }

    /**
     * Get available slots by hospital
     * GET /api/availability/hospital/{hospitalId}/available
     */
    @GetMapping("/hospital/{hospitalId}/available")
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getAvailableSlotsByHospital(
            @PathVariable UUID hospitalId) {
        List<SlotResponse> slots = availabilityService.findAvailableSlotsByHospital(hospitalId);
        return ResponseEntity.ok(
                ResponseHelper.success(slots, "Available slots for hospital retrieved successfully"));
    }

    /**
     * Get available slots by doctor and hospital
     * GET /api/availability/doctor/{doctorId}/hospital/{hospitalId}/available
     */
    @GetMapping("/doctor/{doctorId}/hospital/{hospitalId}/available")
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getAvailableSlotsByDoctorAndHospital(
            @PathVariable UUID doctorId,
            @PathVariable UUID hospitalId) {
        List<SlotResponse> slots = availabilityService.findAvailableSlotsByDoctorAndHospital(
                doctorId, hospitalId);
        return ResponseEntity.ok(
                ResponseHelper.success(slots, 
                        "Available slots for doctor at hospital retrieved successfully"));
    }

    /**
     * Get slots by doctor in a time range
     * GET /api/availability/doctor/{doctorId}/range?startTime=...&endTime=...
     */
    @GetMapping("/doctor/{doctorId}/range")
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getSlotsByDoctorAndTimeRange(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {
        List<SlotResponse> slots = availabilityService.findSlotsByDoctorAndTimeRange(
                doctorId, startTime, endTime);
        return ResponseEntity.ok(
                ResponseHelper.success(slots, "Slots in time range retrieved successfully"));
    }

    /**
     * Mark a slot as booked
     * POST /api/availability/{id}/book
     * Access: ADMIN or DOCTOR (for their own slots)
     */
    @PostMapping("/{id}/book")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<SlotResponse>> bookSlot(@PathVariable UUID id) {
        SlotResponse slot = availabilityService.markAsBooked(id);
        return ResponseEntity.ok(
                ResponseHelper.success(slot, "Slot marked as booked successfully"));
    }

    /**
     * Mark a slot as available
     * POST /api/availability/{id}/available
     * Access: ADMIN or DOCTOR (for their own slots)
     */
    @PostMapping("/{id}/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<SlotResponse>> markSlotAsAvailable(@PathVariable UUID id) {
        SlotResponse slot = availabilityService.markAsAvailable(id);
        return ResponseEntity.ok(
                ResponseHelper.success(slot, "Slot marked as available successfully"));
    }

    /**
     * Update slot status
     * PATCH /api/availability/{id}/status
     * Access: ADMIN or DOCTOR (for their own slots)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<SlotResponse>> updateSlotStatus(
            @PathVariable UUID id,
            @RequestParam SlotStatus status) {
        SlotResponse slot = availabilityService.updateStatus(id, status);
        return ResponseEntity.ok(
                ResponseHelper.success(slot, "Slot status updated successfully"));
    }

    /**
     * Delete a slot
     * DELETE /api/availability/{id}
     * Access: ADMIN or DOCTOR (for their own slots)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<Void>> deleteSlot(@PathVariable UUID id) {
        availabilityService.delete(id);
        return ResponseEntity.ok(
                ResponseHelper.success(null, "Slot deleted successfully"));
    }
}
