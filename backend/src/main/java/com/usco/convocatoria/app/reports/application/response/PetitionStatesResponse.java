package com.usco.convocatoria.app.reports.application.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetitionStatesResponse {

    private String state;
    private Integer countPetitions;
}
    