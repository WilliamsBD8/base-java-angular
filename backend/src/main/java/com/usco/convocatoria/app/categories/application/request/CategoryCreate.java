package com.usco.convocatoria.app.categories.application.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CategoryCreate {

    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "La descripción es requerida")
    private String description;
}
