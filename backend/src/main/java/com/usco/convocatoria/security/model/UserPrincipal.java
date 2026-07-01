package com.usco.convocatoria.security.model;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.usco.convocatoria.app.user.domain.model.RolesEntity;
import com.usco.convocatoria.app.user.domain.model.UserEntity;
import com.usco.convocatoria.app.user.domain.model.enums.StateUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor

public class UserPrincipal implements UserDetails {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private StateUser state;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }


    public static UserPrincipal from(UserEntity user){
        return UserPrincipal.builder()
            .id(user.getId())
            .name(user.getFullName())
            .email(user.getEmail())
            .password(user.getPassword())
            .role(user.getRoles().stream().map(RolesEntity::getName).collect(Collectors.joining(", ")))
            .state(user.getState())
            .authorities(
                user.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .toList()
            )
            .build();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return state.equals(StateUser.ACTIVE);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}
