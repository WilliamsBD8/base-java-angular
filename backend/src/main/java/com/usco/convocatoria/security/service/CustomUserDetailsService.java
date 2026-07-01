package com.usco.convocatoria.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.usco.convocatoria.app.user.domain.repository.UserRepository;
import com.usco.convocatoria.security.model.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        return userRepository.findByEmail(username)
            .map(UserPrincipal::from)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos."));
    }

}
