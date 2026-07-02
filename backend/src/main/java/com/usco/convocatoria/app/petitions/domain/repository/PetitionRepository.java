package com.usco.convocatoria.app.petitions.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.usco.convocatoria.app.petitions.domain.model.PetitionsEntity;
import com.usco.convocatoria.app.petitions.domain.model.enums.PetitionState;
import com.usco.convocatoria.app.user.domain.model.UserEntity;

public interface PetitionRepository extends JpaRepository<PetitionsEntity, Long> {

    @EntityGraph(attributePaths = {
            "convocation",
            "convocation.categories",
            "convocation.createdBy",
            "convocation.createdBy.roles",
            "user",
            "user.roles"
    })
    Page<PetitionsEntity> findByUser_Id(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "convocation",
            "convocation.categories",
            "convocation.createdBy",
            "convocation.createdBy.roles",
            "user",
            "user.roles"
    })
    java.util.Optional<PetitionsEntity> findById(Long id);

    long countByConvocation_IdAndState(Long convocationId, PetitionState state);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    java.util.List<PetitionsEntity> findByConvocation_Id(Long convocationId);

    long countByState(PetitionState state);

    @EntityGraph(attributePaths = {"convocation", "convocation.categories", "convocation.createdBy", "convocation.createdBy.roles", "user", "user.roles"})
    Page<PetitionsEntity> findAllByConvocation_CreatedBy(UserEntity createdBy, Pageable pageable);
}
