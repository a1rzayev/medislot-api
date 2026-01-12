package com.medislot.demo.validation;

import com.medislot.demo.dto.appointment.AppointmentCreateRequest;
import com.medislot.demo.entity.SlotStatus;
import com.medislot.demo.repository.SlotRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidAppointmentTimeValidator implements ConstraintValidator<ValidAppointmentTime, AppointmentCreateRequest> {

    @Autowired
    private SlotRepository slotRepository;

    @Override
    public boolean isValid(AppointmentCreateRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getSlotId() == null) {
            return true; // Let @NotNull handle null validation
        }

        // Check if repository is available (might not be in test contexts)
        if (slotRepository == null) {
            return true;
        }

        // Verify slot exists, is available, and belongs to the specified doctor and hospital
        return slotRepository.findById(request.getSlotId())
                .map(slot -> slot.getStatus() == SlotStatus.AVAILABLE
                        && slot.getDoctorId().equals(request.getDoctorId())
                        && slot.getHospitalId().equals(request.getHospitalId()))
                .orElse(false);
    }
}
