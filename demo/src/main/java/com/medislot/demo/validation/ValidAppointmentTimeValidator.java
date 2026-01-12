package com.medislot.demo.validation;

import com.medislot.demo.dto.appointment.AppointmentCreateRequest;
import com.medislot.demo.entity.SlotStatus;
import com.medislot.demo.repository.SlotRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class ValidAppointmentTimeValidator implements ConstraintValidator<ValidAppointmentTime, AppointmentCreateRequest> {

    @Autowired
    private SlotRepository slotRepository;

    @Override
    public boolean isValid(AppointmentCreateRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getSlotId() == null) {
            return true; // Let @NotNull handle null validation
        }

        // Skip validation if doctorId or hospitalId is null (let @NotNull handle it)
        if (request.getDoctorId() == null || request.getHospitalId() == null) {
            return true;
        }

        // Check if repository is available (might not be in test contexts)
        if (slotRepository == null) {
            return true;
        }

        // Verify slot exists, is available, and belongs to the specified doctor and hospital
        return slotRepository.findById(request.getSlotId())
                .map(slot -> slot.getStatus() == SlotStatus.AVAILABLE
                        && Objects.equals(slot.getDoctorId(), request.getDoctorId())
                        && Objects.equals(slot.getHospitalId(), request.getHospitalId()))
                .orElse(false);
    }
}
