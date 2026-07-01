package com.usco.convocatoria.app.auth.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usco.convocatoria.app.auth.application.requests.LoginRequest;
import com.usco.convocatoria.app.auth.application.requests.RegisterRequest;
import com.usco.convocatoria.app.auth.domain.services.AuthService;
import com.usco.convocatoria.common.response.ApiResponse;
import com.usco.convocatoria.security.model.UserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        HttpStatus.CREATED.value() + "",
                        "Registro exitoso",
                        authService.register(request)
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value() + "",
                        "Login exitoso",
                        authService.login(request)
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value() + "",
                        "Usuario obtenido exitosamente",
                        authService.getCurrentUser(user)
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        authService.logout(authorizationHeader);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value() + "",
                        "Sesión cerrada exitosamente"
                )
        );
    }
}
