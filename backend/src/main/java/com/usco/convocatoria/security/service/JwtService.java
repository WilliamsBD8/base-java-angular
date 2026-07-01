package com.usco.convocatoria.security.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.usco.convocatoria.security.model.UserPrincipal;
import com.usco.convocatoria.security.properties.JwtProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class JwtService {
    private final JwtProperties jwtProperties;

    public String generateToken(UserPrincipal user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put(
                "roles",
                user.getAuthorities()
                        .stream()
                        .map(auth -> auth.getAuthority())
                        .toList()
        );

        return buildToken(claims, user);
    }

    private String buildToken(
        Map<String, Object> claims,
        UserDetails userDetails
    ) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + jwtProperties.getExpiration()
        );

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties
                        .getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(
        String token,
        Function<Claims, T> resolver
    ) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
