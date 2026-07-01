package com.usco.convocatoria.app.reports.application.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetitionConvocationResponse {

    private String convocationName;
    private Integer countPetitions;
    private Integer countPetitionsPending;
    private Integer countPetitionsAccepted;
    private Integer countPetitionsRejected;
}
