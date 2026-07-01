package com.usco.convocatoria.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.usco.convocatoria.security.model.UserPrincipal;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }

        return null;
    }
}
