package com.medislot.medislot.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;

public class ValidTimeRangeValidator implements ConstraintValidator<ValidTimeRange, Object> {

    private String startTimeField;
    private String endTimeField;

    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
        this.startTimeField = constraintAnnotation.startTimeField();
        this.endTimeField = constraintAnnotation.endTimeField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Field startField = value.getClass().getDeclaredField(startTimeField);
            Field endField = value.getClass().getDeclaredField(endTimeField);

            startField.setAccessible(true);
            endField.setAccessible(true);

            Object startTimeObj = startField.get(value);
            Object endTimeObj = endField.get(value);

            if (startTimeObj == null || endTimeObj == null) {
                return true; // Let @NotNull handle null validation
            }

            if (startTimeObj instanceof OffsetDateTime && endTimeObj instanceof OffsetDateTime) {
                OffsetDateTime startTime = (OffsetDateTime) startTimeObj;
                OffsetDateTime endTime = (OffsetDateTime) endTimeObj;
                return endTime.isAfter(startTime);
            }

            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
