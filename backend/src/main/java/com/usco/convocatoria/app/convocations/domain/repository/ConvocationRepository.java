package com.usco.convocatoria.app.convocations.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.usco.convocatoria.app.categories.domain.model.CategoriesEntity;
import com.usco.convocatoria.app.convocations.domain.model.ConvocationsEntity;
import com.usco.convocatoria.app.convocations.domain.model.enums.ConvocationsStates;
import com.usco.convocatoria.app.user.domain.model.UserEntity;

public interface ConvocationRepository extends JpaRepository<ConvocationsEntity, Long> {

    @EntityGraph(attributePaths = {"categories", "createdBy", "createdBy.roles"})
    Page<ConvocationsEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"categories", "createdBy", "createdBy.roles"})
    Optional<ConvocationsEntity> findById(Long id);

    @EntityGraph(attributePaths = {"categories", "createdBy", "createdBy.roles"})
    List<ConvocationsEntity> findByCategories(CategoriesEntity category);

    @Query("""
            SELECT COUNT(c) FROM ConvocationsEntity c
            JOIN c.categories cat
            WHERE cat.id = :categoryId
            """)
    long countByCategoryId(@Param("categoryId") Long categoryId);

    @Query("""
            SELECT COUNT(c) FROM ConvocationsEntity c
            JOIN c.categories cat
            WHERE cat.id = :categoryId AND c.state = :state
            """)
    long countByCategoryIdAndState(
            @Param("categoryId") Long categoryId,
            @Param("state") ConvocationsStates state
    );

    @EntityGraph(attributePaths = {"categories", "createdBy", "createdBy.roles"})
    Page<ConvocationsEntity> findAllByCreatedBy(UserEntity createdBy, Pageable pageable);
}
