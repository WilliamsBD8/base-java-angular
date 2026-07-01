package com.usco.convocatoria.app.petitions.application.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PetitionCreate {

    @NotNull(message = "La convocatoria es requerida")
    private Long convocationId;

}
