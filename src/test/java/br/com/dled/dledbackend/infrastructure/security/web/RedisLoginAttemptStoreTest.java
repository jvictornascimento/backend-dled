package br.com.dled.dledbackend.infrastructure.security.web;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class RedisLoginAttemptStoreTest {

    @Test
    void shouldSaveAttemptWithTtl() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        RedisLoginAttemptStore store = new RedisLoginAttemptStore(redisTemplate);

        Instant blockedUntil = Instant.parse("2026-09-14T00:10:00Z");
        Duration ttl = Duration.ofMinutes(10);

        store.save("login-attempt:user:10.0.0.1", new LoginAttempt(5, blockedUntil), ttl);

        verify(operations).set("login-attempt:user:10.0.0.1", "5:1789344600000", ttl);
    }

    @Test
    void shouldReadAttemptFromRedis() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        when(operations.get("login-attempt:user:10.0.0.1")).thenReturn("3:");
        RedisLoginAttemptStore store = new RedisLoginAttemptStore(redisTemplate);

        LoginAttempt attempt = store.find("login-attempt:user:10.0.0.1").orElseThrow();

        assertThat(attempt.failedAttempts()).isEqualTo(3);
        assertThat(attempt.blockedUntil()).isNull();
    }

    @Test
    void shouldDeleteMalformedRedisValue() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        when(operations.get("login-attempt:user:10.0.0.1")).thenReturn("invalid:1789344600000");
        RedisLoginAttemptStore store = new RedisLoginAttemptStore(redisTemplate);

        assertThat(store.find("login-attempt:user:10.0.0.1")).isEmpty();
        verify(redisTemplate).delete("login-attempt:user:10.0.0.1");
    }
}
