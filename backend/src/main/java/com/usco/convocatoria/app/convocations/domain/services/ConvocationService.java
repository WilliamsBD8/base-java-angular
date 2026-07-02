package com.usco.convocatoria.app.convocations.domain.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usco.convocatoria.app.categories.domain.model.CategoriesEntity;
import com.usco.convocatoria.app.categories.domain.repository.CategoryRepository;
import com.usco.convocatoria.app.convocations.application.request.ConvocationCreate;
import com.usco.convocatoria.app.convocations.application.request.ConvocationUpdate;
import com.usco.convocatoria.app.convocations.application.response.ConvocationResponse;
import com.usco.convocatoria.app.convocations.domain.model.ConvocationsEntity;
import com.usco.convocatoria.app.convocations.domain.model.enums.ConvocationsStates;
import com.usco.convocatoria.app.convocations.domain.repository.ConvocationRepository;
import com.usco.convocatoria.app.petitions.application.response.PetitionResponse;
import com.usco.convocatoria.app.petitions.domain.repository.PetitionRepository;
import com.usco.convocatoria.app.user.domain.service.UserService;
import com.usco.convocatoria.common.mapper.ResponseMapper;
import com.usco.convocatoria.common.response.ApiPage;
import com.usco.convocatoria.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConvocationService {

    private final CategoryRepository categoryRepository;
    private final ConvocationRepository convocationRepository;
    private final PetitionRepository petitionRepository;
    private final UserService userService;
    private final ResponseMapper responseMapper;

    public ConvocationsEntity createConvocation(ConvocationCreate request) {
        ConvocationsEntity convocation = new ConvocationsEntity();
        convocation.setName(request.getName());
        convocation.setDescription(request.getDescription());
        convocation.setInitialDate(request.getInitialDate());
        convocation.setFinalDate(request.getFinalDate());
        convocation.setQuota(request.getQuota());
        convocation.setCreatedBy(userService.getCurrentUser());
        convocation.setCategories(resolveCategories(request.getCategories()));
        convocation.setState(ConvocationsStates.BORRADOR);
        return convocationRepository.save(convocation);
    }

    public ConvocationsEntity findConvocationById(Long id) {
        return convocationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Convocatoria no encontrada", null));
    }

    public ConvocationsEntity updateConvocation(Long id, ConvocationUpdate request) {
        ConvocationsEntity convocation = findConvocationById(id);

        if (!Objects.equals(convocation.getCreatedBy().getId(), userService.getCurrentUser().getId())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "No tienes permisos para actualizar esta convocatoria", null);
        }

        if (convocation.getState() == ConvocationsStates.PUBLICADA
                || convocation.getState() == ConvocationsStates.CERRADA) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "La convocatoria no puede ser actualizada",
                    List.of(Map.of("state", "La convocatoria no puede ser actualizada, debe estar en estado borrador"))
            );
        }

        convocation.setName(request.getName() != null ? request.getName() : convocation.getName());
        convocation.setDescription(request.getDescription() != null ? request.getDescription() : convocation.getDescription());
        convocation.setInitialDate(request.getInitialDate() != null ? request.getInitialDate() : convocation.getInitialDate());
        convocation.setFinalDate(request.getFinalDate() != null ? request.getFinalDate() : convocation.getFinalDate());
        convocation.setQuota(request.getQuota() != null ? request.getQuota() : convocation.getQuota());

        if (request.getCategories() != null) {
            convocation.setCategories(resolveCategories(request.getCategories()));
        }

        return convocationRepository.save(convocation);
    }

    public void deleteConvocation(Long id) {
        ConvocationsEntity convocation = findConvocationById(id);
        convocationRepository.delete(convocation);
    }

    @Transactional(readOnly = true)
    public ApiPage<ConvocationResponse> findAllConvocations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if(userService.getCurrentUser().getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN")) || userService.getCurrentUser().getRoles().stream().anyMatch(role -> role.getName().equals("STUDENT"))) {
            Page<ConvocationsEntity> convocations = convocationRepository.findAll(pageable);
            return ApiPage.of(convocations.map(this::fromEntity));
        } else {
            Page<ConvocationsEntity> convocations = convocationRepository.findAllByCreatedBy(userService.getCurrentUser(), pageable);
            return ApiPage.of(convocations.map(this::fromEntity));
        }
    }

    public ConvocationsEntity updateConvocationState(Long id, ConvocationsStates state) {
        ConvocationsEntity convocation = findConvocationById(id);

        if (!Objects.equals(convocation.getCreatedBy().getId(), userService.getCurrentUser().getId())) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos para actualizar el estado de esta convocatoria",
                    null
            );
        }

        if (state == ConvocationsStates.PUBLICADA && convocation.getState() != ConvocationsStates.BORRADOR) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "La convocatoria no puede ser publicada",
                    List.of(Map.of("state", "La convocatoria no puede ser publicada, debe estar en estado borrador"))
            );
        }

        if (state == ConvocationsStates.CERRADA && convocation.getState() != ConvocationsStates.PUBLICADA) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "La convocatoria no puede ser cerrada",
                    List.of(Map.of("state", "La convocatoria no puede ser cerrada, debe estar en estado publicada"))
            );
        }

        convocation.setState(state);
        return convocationRepository.save(convocation);
    }

    @Transactional(readOnly = true)
    public ConvocationResponse fromEntity(ConvocationsEntity entity) {
        List<PetitionResponse> petitions = petitionRepository.findByConvocation_Id(entity.getId())
                .stream()
                .map(responseMapper::toPetitionSummary)
                .toList();

        return responseMapper.toConvocationResponse(entity, petitions);
    }

    private java.util.Set<CategoriesEntity> resolveCategories(Collection<Long> categoryIds) {
        return categoryIds.stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Categoría no encontrada",
                                List.of(Map.of("categoryId", "Una de las categorías no existe"))
                        )))
                .collect(Collectors.toSet());
    }
}
