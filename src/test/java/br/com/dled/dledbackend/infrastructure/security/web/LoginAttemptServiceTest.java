package br.com.dled.dledbackend.infrastructure.security.web;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginAttemptServiceTest {

    @Test
    void shouldBlockLoginAfterConfiguredFailedAttemptsForUserAndClientAddress() {
        MutableClock clock = new MutableClock();
        FakeLoginAttemptStore store = new FakeLoginAttemptStore(clock);
        LoginAttemptService service = new LoginAttemptService(clock, store, properties());

        service.loginFailed("User", "10.0.0.1");
        service.loginFailed(" user ", "10.0.0.1");

        assertThatThrownBy(() -> service.ensureLoginAllowed("USER", "10.0.0.1"))
                .isInstanceOf(TooManyLoginAttemptsException.class);
        assertThatCode(() -> service.ensureLoginAllowed("USER", "10.0.0.2"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldClearAttemptsAfterSuccessfulLogin() {
        MutableClock clock = new MutableClock();
        FakeLoginAttemptStore store = new FakeLoginAttemptStore(clock);
        LoginAttemptService service = new LoginAttemptService(clock, store, properties());

        service.loginFailed("user", "10.0.0.1");
        service.loginSucceeded("user", "10.0.0.1");

        assertThatCode(() -> service.ensureLoginAllowed("user", "10.0.0.1"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldExpireFailedAttemptsAfterFailureWindow() {
        MutableClock clock = new MutableClock();
        FakeLoginAttemptStore store = new FakeLoginAttemptStore(clock);
        LoginAttemptService service = new LoginAttemptService(clock, store, properties());

        service.loginFailed("user", "10.0.0.1");
        clock.advance(Duration.ofMinutes(6));
        service.loginFailed("user", "10.0.0.1");

        assertThatCode(() -> service.ensureLoginAllowed("user", "10.0.0.1"))
                .doesNotThrowAnyException();
    }

    private LoginRateLimitProperties properties() {
        LoginRateLimitProperties properties = new LoginRateLimitProperties();
        properties.setMaxFailedAttempts(2);
        properties.setBlockMinutes(10);
        properties.setFailureWindowMinutes(5);
        return properties;
    }

    private static class FakeLoginAttemptStore implements LoginAttemptStore {
        private final Clock clock;
        private final Map<String, StoredAttempt> attempts = new HashMap<>();

        private FakeLoginAttemptStore(Clock clock) {
            this.clock = clock;
        }

        @Override
        public Optional<LoginAttempt> find(String key) {
            StoredAttempt stored = attempts.get(key);
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
            attempts.put(key, new StoredAttempt(attempt, clock.instant().plus(ttl)));
        }

        @Override
        public void delete(String key) {
            attempts.remove(key);
        }
    }

    private record StoredAttempt(LoginAttempt attempt, Instant expiresAt) {
    }

    private static class MutableClock extends Clock {
        private Instant instant = Instant.parse("2026-09-14T00:00:00Z");

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }

        private void advance(Duration duration) {
            instant = instant.plus(duration);
        }
    }
}
