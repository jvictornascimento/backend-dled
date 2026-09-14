package br.com.dled.dledbackend.infrastructure.security.web;

import java.time.Instant;

public record LoginAttempt(int failedAttempts, Instant blockedUntil) {
    boolean isBlocked(Instant now) {
        return blockedUntil != null && blockedUntil.isAfter(now);
    }
}
