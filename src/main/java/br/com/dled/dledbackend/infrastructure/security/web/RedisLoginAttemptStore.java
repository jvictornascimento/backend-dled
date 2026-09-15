package br.com.dled.dledbackend.infrastructure.security.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "security.login-rate-limit", name = "store", havingValue = "redis")
public class RedisLoginAttemptStore implements LoginAttemptStore {
    private static final String FIELD_SEPARATOR = ":";

    private final StringRedisTemplate redisTemplate;

    public RedisLoginAttemptStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<LoginAttempt> find(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        String[] parts = value.split(FIELD_SEPARATOR, -1);
        if (parts.length != 2) {
            delete(key);
            return Optional.empty();
        }

        try {
            int failedAttempts = Integer.parseInt(parts[0]);
            Instant blockedUntil = parts[1].isBlank() ? null : Instant.ofEpochMilli(Long.parseLong(parts[1]));
            return Optional.of(new LoginAttempt(failedAttempts, blockedUntil));
        } catch (NumberFormatException ex) {
            delete(key);
            return Optional.empty();
        }
    }

    @Override
    public void save(String key, LoginAttempt attempt, Duration ttl) {
        String blockedUntil = attempt.blockedUntil() == null ? "" : String.valueOf(attempt.blockedUntil().toEpochMilli());
        redisTemplate.opsForValue().set(key, attempt.failedAttempts() + FIELD_SEPARATOR + blockedUntil, ttl);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
