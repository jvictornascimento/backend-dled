package br.com.dled.dledbackend.infrastructure.security.web;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(LoginRateLimitProperties.class)
public class LoginAttemptService {
    private static final String KEY_PREFIX = "login-attempt:";

    private final Clock clock;
    private final LoginAttemptStore store;
    private final LoginRateLimitProperties properties;

    public void ensureLoginAllowed(String username, String clientAddress) {
        String key = buildKey(username, clientAddress);
        LoginAttempt attempt = store.find(key).orElse(null);
        if (attempt != null && attempt.isBlocked(clock.instant())) {
            throw new TooManyLoginAttemptsException("Too many failed login attempts. Try again later.");
        }
    }

    public void loginFailed(String username, String clientAddress) {
        String key = buildKey(username, clientAddress);
        Instant now = clock.instant();
        LoginAttempt current = store.find(key)
                .filter(attempt -> attempt.blockedUntil() == null || attempt.blockedUntil().isAfter(now))
                .orElse(null);

        int failedAttempts = current == null ? 1 : current.failedAttempts() + 1;
        Instant blockedUntil = failedAttempts >= properties.getMaxFailedAttempts()
                ? now.plus(blockDuration())
                : null;
        Duration ttl = blockedUntil == null ? failureWindowDuration() : blockDuration();

        store.save(key, new LoginAttempt(failedAttempts, blockedUntil), ttl);
    }

    public void loginSucceeded(String username, String clientAddress) {
        store.delete(buildKey(username, clientAddress));
    }

    private String buildKey(String username, String clientAddress) {
        return KEY_PREFIX + sha256(normalize(username) + ":" + normalize(clientAddress));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Could not create login rate limit key.", exception);
        }
    }

    private Duration blockDuration() {
        return Duration.ofMinutes(properties.getBlockMinutes());
    }

    private Duration failureWindowDuration() {
        return Duration.ofMinutes(properties.getFailureWindowMinutes());
    }
}
