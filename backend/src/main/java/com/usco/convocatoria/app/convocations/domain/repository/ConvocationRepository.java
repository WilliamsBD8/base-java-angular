package com.usco.convocatoria.app.convocations.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.usco.convocatoria.app.convocations.domain.model.ConvocationsEntity;

public interface ConvocationRepository extends JpaRepository<ConvocationsEntity, Long> {

    @EntityGraph(attributePaths = {"categories", "createdBy", "createdBy.roles"})
    Page<ConvocationsEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"categories", "createdBy", "createdBy.roles"})
    Optional<ConvocationsEntity> findById(Long id);
}
