package com.usco.convocatoria.app.categories.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usco.convocatoria.app.categories.domain.model.CategoriesEntity;

public interface CategoryRepository extends JpaRepository<CategoriesEntity, Long> {

    Optional<CategoriesEntity> findByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
