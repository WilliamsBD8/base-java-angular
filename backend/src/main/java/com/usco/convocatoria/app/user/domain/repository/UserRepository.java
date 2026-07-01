package com.usco.convocatoria.app.user.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.usco.convocatoria.app.user.domain.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
