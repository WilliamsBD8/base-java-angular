package com.usco.convocatoria.common.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.usco.convocatoria.app.categories.application.response.CategoryResponse;
import com.usco.convocatoria.app.categories.domain.model.CategoriesEntity;
import com.usco.convocatoria.app.convocations.application.response.ConvocationResponse;
import com.usco.convocatoria.app.convocations.domain.model.ConvocationsEntity;
import com.usco.convocatoria.app.petitions.application.response.PetitionResponse;
import com.usco.convocatoria.app.petitions.domain.model.PetitionsEntity;
import com.usco.convocatoria.app.user.application.response.UserResponseApp;
import com.usco.convocatoria.app.user.domain.model.RolesEntity;
import com.usco.convocatoria.app.user.domain.model.UserEntity;

@Component
public class ResponseMapper {

    public ConvocationResponse toConvocationResponse(
            ConvocationsEntity entity,
            List<PetitionResponse> petitions
    ) {
        return ConvocationResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .initialDate(entity.getInitialDate())
                .finalDate(entity.getFinalDate())
                .quota(entity.getQuota())
                .state(entity.getState())
                .categories(entity.getCategories().stream().map(this::toCategoryResponse).toList())
                .petitions(petitions)
                .createdBy(toUserResponse(entity.getCreatedBy()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ConvocationResponse toConvocationSummary(ConvocationsEntity entity) {
        return toConvocationResponse(entity, null);
    }

    public PetitionResponse toPetitionResponse(
            PetitionsEntity entity,
            ConvocationResponse convocation
    ) {
        return PetitionResponse.builder()
                .id(entity.getId())
                .convocation(convocation)
                .user(toUserResponse(entity.getUser()))
                .state(entity.getState())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public PetitionResponse toPetitionSummary(PetitionsEntity entity) {
        return PetitionResponse.builder()
                .id(entity.getId())
                .user(toUserResponse(entity.getUser()))
                .state(entity.getState())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CategoryResponse toCategoryResponse(CategoriesEntity category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public UserResponseApp toUserResponse(UserEntity user) {
        return UserResponseApp.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(RolesEntity::getName).toList())
                .state(user.getState())
                .build();
    }
}
