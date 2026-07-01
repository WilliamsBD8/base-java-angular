package com.usco.convocatoria.app.convocations.application.request;

import java.time.LocalDateTime;
import java.util.Set;

import com.usco.convocatoria.app.convocations.application.validation.ConvocationDates;
import com.usco.convocatoria.app.convocations.application.validation.ValidConvocationDates;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@ValidConvocationDates
public class ConvocationCreate implements ConvocationDates {

    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "La descripción es requerida")
    private String description;

    @NotNull(message = "La fecha inicial es requerida")
    private LocalDateTime initialDate;

    @NotNull(message = "La fecha final es requerida")
    private LocalDateTime finalDate;

    @NotNull(message = "El número de cupos es requerido")
    @Positive(message = "El número de cupos debe ser mayor que 0")
    private Integer quota;

    @NotEmpty(message = "Las categorías son requeridas")
    @Size(min = 1, message = "Debe seleccionar al menos una categoría")
    private Set<Long> categories;
}
