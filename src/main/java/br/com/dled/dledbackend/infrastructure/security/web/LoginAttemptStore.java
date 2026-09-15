package br.com.dled.dledbackend.infrastructure.security.web;

import java.time.Duration;
import java.util.Optional;

public interface LoginAttemptStore {
    Optional<LoginAttempt> find(String key);
    void save(String key, LoginAttempt attempt, Duration ttl);
    void delete(String key);
}
