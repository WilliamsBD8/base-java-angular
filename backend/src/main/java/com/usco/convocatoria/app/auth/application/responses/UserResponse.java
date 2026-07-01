package com.usco.convocatoria.app.auth.application.responses;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.usco.convocatoria.app.user.domain.model.enums.StateUser;
import com.usco.convocatoria.security.model.UserPrincipal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String name;
    private String email;
    private List<String> roles;
    private StateUser state;

    public static UserResponse from(UserPrincipal user) {
        return UserResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .state(user.getState())
                .build();
    }
}
