package com.usco.convocatoria.app.reports.domain.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usco.convocatoria.app.categories.domain.repository.CategoryRepository;
import com.usco.convocatoria.app.convocations.domain.model.enums.ConvocationsStates;
import com.usco.convocatoria.app.convocations.domain.repository.ConvocationRepository;
import com.usco.convocatoria.app.petitions.domain.model.enums.PetitionState;
import com.usco.convocatoria.app.petitions.domain.repository.PetitionRepository;
import com.usco.convocatoria.app.reports.application.response.ConvocationCategoryResponse;
import com.usco.convocatoria.app.reports.application.response.PetitionConvocationResponse;
import com.usco.convocatoria.app.reports.application.response.PetitionStatesResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final CategoryRepository categoryRepository;
    private final ConvocationRepository convocationRepository;
    private final PetitionRepository petitionRepository;

    public List<ConvocationCategoryResponse> getConvocationCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    long countDraft = convocationRepository.countByCategoryIdAndState(
                            category.getId(),
                            ConvocationsStates.BORRADOR
                    );
                    long countPublished = convocationRepository.countByCategoryIdAndState(
                            category.getId(),
                            ConvocationsStates.PUBLICADA
                    );
                    long countClosed = convocationRepository.countByCategoryIdAndState(
                            category.getId(),
                            ConvocationsStates.CERRADA
                    );

                    return ConvocationCategoryResponse.builder()
                            .category(category.getName())
                            .count((int) convocationRepository.countByCategoryId(category.getId()))
                            .countDraft((int) countDraft)
                            .countPublished((int) countPublished)
                            .countClosed((int) countClosed)
                            .build();
                })
                .toList();
    }
    
    public List<PetitionConvocationResponse> getPetitionConvocation() {
        return convocationRepository.findAll().stream()
                .map(convocation -> {
                    return PetitionConvocationResponse.builder()
                            .convocationName(convocation.getName())
                            .countPetitions((int) convocation.getPetitions().size())
                            .countPetitionsPending((int) convocation.getPetitions().stream()
                                    .filter(petition -> petition.getState() == PetitionState.PENDIENTE)
                                    .count())
                            .countPetitionsAccepted((int) convocation.getPetitions().stream()
                                    .filter(petition -> petition.getState() == PetitionState.APROBADA)
                                    .count())
                            .countPetitionsRejected((int) convocation.getPetitions().stream()
                                    .filter(petition -> petition.getState() == PetitionState.RECHAZADA)
                                    .count())
                            .build();
                })
                .toList();
    }

    public List<PetitionStatesResponse> getPetitionStates() {
        return Arrays.stream(PetitionState.values())
                .map(state -> {
                    long count = petitionRepository.countByState(state);
                    return PetitionStatesResponse.builder()
                            .state(state.name())
                            .countPetitions((int) count)
                            .build();
                })
                .toList();
    }
}
