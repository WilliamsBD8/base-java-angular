package com.usco.convocatoria.app.auth.domain.services;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usco.convocatoria.app.auth.application.requests.LoginRequest;
import com.usco.convocatoria.app.auth.application.requests.RegisterRequest;
import com.usco.convocatoria.app.auth.application.responses.LoginResponse;
import com.usco.convocatoria.app.auth.application.responses.UserResponse;
import com.usco.convocatoria.app.user.domain.model.RolesEntity;
import com.usco.convocatoria.app.user.domain.model.UserEntity;
import com.usco.convocatoria.app.user.domain.repository.RoleRepository;
import com.usco.convocatoria.app.user.domain.repository.UserRepository;
import com.usco.convocatoria.exception.BusinessException;
import com.usco.convocatoria.security.model.UserPrincipal;
import com.usco.convocatoria.security.service.JwtService;
import com.usco.convocatoria.security.service.TokenBlacklistService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return buildLoginResponse(userPrincipal);
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(HttpStatus.CONFLICT, "El email ya está registrado", null);
        }

        RolesEntity role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Rol " + request.getRole() + " no configurado",
                        null
                ));

        UserEntity user = new UserEntity();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(role));

        UserEntity savedUser = userRepository.save(user);
        UserPrincipal userPrincipal = UserPrincipal.from(savedUser);

        return buildLoginResponse(userPrincipal);
    }

    public UserResponse getCurrentUser(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "No autenticado", null);
        }

        return UserResponse.from(userPrincipal);
    }

    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Token no proporcionado", null);
        }

        String token = authorizationHeader.substring(7);

        try {
            tokenBlacklistService.blacklist(token, jwtService.extractExpiration(token).toInstant());
        } catch (Exception ex) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Token inválido", null);
        }
    }

    private LoginResponse buildLoginResponse(UserPrincipal userPrincipal) {
        String token = jwtService.generateToken(userPrincipal);

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(userPrincipal.getId())
                .name(userPrincipal.getName())
                .email(userPrincipal.getEmail())
                .roles(userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }
}
