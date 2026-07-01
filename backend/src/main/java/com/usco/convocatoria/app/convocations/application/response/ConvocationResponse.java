package com.usco.convocatoria.app.convocations.application.response;

import java.time.LocalDateTime;
import java.util.List;

import com.usco.convocatoria.app.categories.application.response.CategoryResponse;
import com.usco.convocatoria.app.convocations.domain.model.enums.ConvocationsStates;
import com.usco.convocatoria.app.petitions.application.response.PetitionResponse;
import com.usco.convocatoria.app.user.application.response.UserResponseApp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ConvocationResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    private Integer quota;
    private ConvocationsStates state;
    private List<CategoryResponse> categories;
    private List<PetitionResponse> petitions;
    private UserResponseApp createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
