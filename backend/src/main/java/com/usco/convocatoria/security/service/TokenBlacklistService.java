package com.usco.convocatoria.security.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklist(String token, Instant expiresAt) {
        blacklistedTokens.put(token, expiresAt);
    }

    public boolean isBlacklisted(String token) {
        removeExpiredTokens();
        return blacklistedTokens.containsKey(token);
    }

    private void removeExpiredTokens() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
