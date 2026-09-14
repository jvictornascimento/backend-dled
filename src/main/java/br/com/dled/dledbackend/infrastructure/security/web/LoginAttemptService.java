package br.com.dled.dledbackend.infrastructure.security.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int BLOCK_MINUTES = 15;

    private final Clock clock;
    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public void ensureLoginAllowed(String username) {
        LoginAttempt attempt = attempts.get(normalize(username));
        if (attempt != null && attempt.isBlocked(clock.instant())) {
            throw new TooManyLoginAttemptsException("Too many failed login attempts. Try again later.");
        }
    }

    public void loginFailed(String username) {
        attempts.compute(normalize(username), (key, current) -> {
            Instant now = clock.instant();
            if (current == null || current.blockedUntil() != null && current.blockedUntil().isBefore(now)) {
                return new LoginAttempt(1, null);
            }

            int failedAttempts = current.failedAttempts() + 1;
            Instant blockedUntil = failedAttempts >= MAX_FAILED_ATTEMPTS ? now.plus(BLOCK_MINUTES, ChronoUnit.MINUTES) : null;
            return new LoginAttempt(failedAttempts, blockedUntil);
        });
    }

    public void loginSucceeded(String username) {
        attempts.remove(normalize(username));
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }

    private record LoginAttempt(int failedAttempts, Instant blockedUntil) {
        boolean isBlocked(Instant now) {
            return blockedUntil != null && blockedUntil.isAfter(now);
        }
    }
}
