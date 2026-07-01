package com.usco.convocatoria.app.petitions.application.response;

import java.time.LocalDateTime;

import com.usco.convocatoria.app.convocations.application.response.ConvocationResponse;
import com.usco.convocatoria.app.petitions.domain.model.enums.PetitionState;
import com.usco.convocatoria.app.user.application.response.UserResponseApp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PetitionResponse {

    private Long id;
    private ConvocationResponse convocation;
    private UserResponseApp user;
    private PetitionState state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
