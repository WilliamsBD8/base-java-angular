package com.usco.convocatoria.app.convocations.application.validation;

import java.time.LocalDateTime;

public interface ConvocationDates {

    LocalDateTime getInitialDate();

    LocalDateTime getFinalDate();
}
