package com.medislot.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTimeRangeValidator.class)
@Documented
public @interface ValidTimeRange {
    String message() default "End time must be after start time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String startTimeField() default "startTime";
    String endTimeField() default "endTime";
}
