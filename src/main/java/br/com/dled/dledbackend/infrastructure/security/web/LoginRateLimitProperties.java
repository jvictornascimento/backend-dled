package br.com.dled.dledbackend.infrastructure.security.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "security.login-rate-limit")
public class LoginRateLimitProperties {
    private String store = "memory";
    private int maxFailedAttempts = 5;
    private int blockMinutes = 15;
    private int failureWindowMinutes = 15;
}
