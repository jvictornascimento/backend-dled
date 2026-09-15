package br.com.dled.dledbackend.infrastructure.security.web;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "security.login-rate-limit", name = "store", havingValue = "memory", matchIfMissing = true)
public class InMemoryLoginAttemptStore implements LoginAttemptStore {
    private final ConcurrentMap<String, StoredLoginAttempt> attempts = new ConcurrentHashMap<>();
    private final Clock clock;

    @Override
    public Optional<LoginAttempt> find(String key) {
        StoredLoginAttempt stored = attempts.get(key);
        if (stored == null) {
            return Optional.empty();
        }
        if (stored.expiresAt().isBefore(clock.instant())) {
            attempts.remove(key);
            return Optional.empty();
        }
        return Optional.of(stored.attempt());
    }

    @Override
    public void save(String key, LoginAttempt attempt, Duration ttl) {
        attempts.put(key, new StoredLoginAttempt(attempt, clock.instant().plus(ttl)));
    }

    @Override
    public void delete(String key) {
        attempts.remove(key);
    }

    private record StoredLoginAttempt(LoginAttempt attempt, Instant expiresAt) {
    }
}
