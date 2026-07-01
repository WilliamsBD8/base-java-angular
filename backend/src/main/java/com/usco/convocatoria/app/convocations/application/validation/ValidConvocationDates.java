package com.usco.convocatoria.app.convocations.application.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ValidConvocationDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface ValidConvocationDates {

    String message() default "La fecha final debe ser mayor que la inicial";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}