package com.medislot.demo.validation;

import com.medislot.demo.repository.PatientRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true; // Let @NotBlank handle empty validation
        }

        // Check if repository is available (might not be in test contexts)
        if (patientRepository == null) {
            return true;
        }

        return !patientRepository.existsByEmail(email);
    }
}
