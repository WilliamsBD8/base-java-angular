package com.usco.convocatoria.app.convocations.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidConvocationDatesValidator
        implements ConstraintValidator<ValidConvocationDates, ConvocationDates> {

    @Override
    public boolean isValid(ConvocationDates value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.getInitialDate() == null || value.getFinalDate() == null) {
            return true;
        }

        if (value.getFinalDate().isAfter(value.getInitialDate())) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        "La fecha final debe ser mayor que la inicial.")
                .addPropertyNode("finalDate")
                .addConstraintViolation();

        return false;
    }
}
