package com.usco.convocatoria.app.auth.application.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class LoginResponse {

    private String token;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private List<String> roles;
}
