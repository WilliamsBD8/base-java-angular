package com.usco.convocatoria.app.user.domain.service;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.usco.convocatoria.app.user.application.response.UserResponseApp;
import com.usco.convocatoria.app.user.domain.model.RolesEntity;
import com.usco.convocatoria.app.user.domain.model.UserEntity;
import com.usco.convocatoria.app.user.domain.repository.UserRepository;
import com.usco.convocatoria.exception.BusinessException;
import com.usco.convocatoria.security.model.UserPrincipal;
import com.usco.convocatoria.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getCurrentUser() {
        UserPrincipal userPrincipal = getAuthenticatedUser();

        return userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Usuario no encontrado", null));
    }

    public UserResponseApp getCurrentUserResponse() {
        UserEntity user = getCurrentUser();

        return UserResponseApp.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(RolesEntity::getName).collect(Collectors.toList()))
                .state(user.getState())
                .build();
    }

    private UserPrincipal getAuthenticatedUser() {
        UserPrincipal userPrincipal = SecurityUtils.getCurrentUser();

        if (userPrincipal == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "No autenticado", null);
        }

        return userPrincipal;
    }
}
