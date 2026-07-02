package com.usco.convocatoria.app.petitions.domain.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usco.convocatoria.app.convocations.domain.model.ConvocationsEntity;
import com.usco.convocatoria.app.convocations.domain.model.enums.ConvocationsStates;
import com.usco.convocatoria.app.convocations.domain.repository.ConvocationRepository;
import com.usco.convocatoria.app.petitions.application.request.PetitionCreate;
import com.usco.convocatoria.app.petitions.application.request.PetitionUpdate;
import com.usco.convocatoria.app.petitions.application.response.PetitionResponse;
import com.usco.convocatoria.app.petitions.domain.model.PetitionsEntity;
import com.usco.convocatoria.app.petitions.domain.model.enums.PetitionState;
import com.usco.convocatoria.app.petitions.domain.repository.PetitionRepository;
import com.usco.convocatoria.app.user.domain.model.UserEntity;
import com.usco.convocatoria.app.user.domain.service.UserService;
import com.usco.convocatoria.common.mapper.ResponseMapper;
import com.usco.convocatoria.common.response.ApiPage;
import com.usco.convocatoria.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PetitionService {

    private final PetitionRepository petitionRepository;
    private final ConvocationRepository convocationRepository;
    private final UserService userService;
    private final ResponseMapper responseMapper;

    public PetitionsEntity createPetition(PetitionCreate request) {
        ConvocationsEntity convocation = findConvocationById(request.getConvocationId());

        if (convocation.getState() == ConvocationsStates.CERRADA) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La convocatoria está cerrada, no se puede inscribir", null);
        }

        if (convocation.getState() != ConvocationsStates.PUBLICADA) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La convocatoria no está publicada, no se puede inscribir", null);
        }

        validateQuotaAvailable(convocation.getId(), convocation.getQuota());

        PetitionsEntity petition = new PetitionsEntity();
        petition.setConvocation(convocation);
        petition.setUser(userService.getCurrentUser());
        petition.setState(PetitionState.PENDIENTE);

        return petitionRepository.save(petition);
    }

    public PetitionsEntity findPetitionById(Long id) {
        return petitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Petición no encontrada", null));
    }

    @Transactional(readOnly = true)
    public PetitionResponse findPetition(Long id) {
        UserEntity currentUser = userService.getCurrentUser();
        PetitionsEntity petition = findPetitionById(id);
        if (!Objects.equals(petition.getUser().getId(), currentUser.getId())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "No tienes permisos para ver esta petición", null);
        }
        return fromEntity(petition);
    }

    @Transactional(readOnly = true)
    public PetitionResponse fromEntity(PetitionsEntity entity) {
        return responseMapper.toPetitionResponse(
                entity,
                responseMapper.toConvocationSummary(entity.getConvocation())
        );
    }

    public PetitionsEntity updatePetition(Long id, PetitionUpdate request) {
        PetitionsEntity petition = findPetitionById(id);
        UserEntity currentUser = userService.getCurrentUser();

        if (!Objects.equals(petition.getConvocation().getCreatedBy().getId(), currentUser.getId())
                && !Objects.equals(petition.getUser().getId(), currentUser.getId())) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos para actualizar esta petición",
                    List.of(Map.of("state", "No eres el creador de la convocatoria o el dueño de la petición"))
            );
        }

        if (petition.getState() == PetitionState.APROBADA) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "La petición ya ha sido aceptada",
                    List.of(Map.of("state", "La petición ya ha sido aceptada"))
            );
        }

        if (petition.getState() == PetitionState.RECHAZADA) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "La petición ya ha sido rechazada",
                    List.of(Map.of("state", "La petición ya ha sido rechazada"))
            );
        }

        if (request.getState() == PetitionState.APROBADA) {
            validateQuotaAvailable(petition.getConvocation().getId(), petition.getConvocation().getQuota());
        }

        petition.setState(request.getState());
        return petitionRepository.save(petition);
    }

    @Transactional(readOnly = true)
    public ApiPage<PetitionResponse> findAllPetitions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        UserEntity currentUser = userService.getCurrentUser();
        if(currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            Page<PetitionsEntity> petitions = petitionRepository.findAll(pageable);
            return ApiPage.of(petitions.map(this::fromEntity));
        } else if(currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("TEACHER"))) {
            Page<PetitionsEntity> petitions = petitionRepository.findAllByConvocation_CreatedBy(currentUser, pageable);
            return ApiPage.of(petitions.map(this::fromEntity));
        } else {
            Page<PetitionsEntity> petitions = petitionRepository.findByUser_Id(currentUser.getId(), pageable);
            return ApiPage.of(petitions.map(this::fromEntity));
        }    
    }

    private ConvocationsEntity findConvocationById(Long id) {
        return convocationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Convocatoria no encontrada", null));
    }

    private void validateQuotaAvailable(Long convocationId, Integer quota) {
        long acceptedPetitions = petitionRepository.countByConvocation_IdAndState(
                convocationId,
                PetitionState.APROBADA
        );

        if (quota <= acceptedPetitions) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La convocatoria está llena, no se puede inscribir", null);
        }
    }
}
