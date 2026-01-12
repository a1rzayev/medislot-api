package com.medislot.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAppointmentTimeValidator.class)
@Documented
public @interface ValidAppointmentTime {
    String message() default "Appointment time is not valid or not within doctor's availability";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
