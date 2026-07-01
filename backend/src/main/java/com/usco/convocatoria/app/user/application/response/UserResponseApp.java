package com.usco.convocatoria.app.user.application.response;

import java.util.List;

import com.usco.convocatoria.app.user.domain.model.enums.StateUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserResponseApp {

    private Long userId;
    private String fullName;
    private String email;
    private List<String> roles;
    private StateUser status;
}
