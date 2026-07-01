package com.usco.convocatoria.app.user.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usco.convocatoria.app.user.domain.model.RolesEntity;

public interface RoleRepository extends JpaRepository<RolesEntity, Long> {

    Optional<RolesEntity> findByName(String name);
}
