package br.com.dled.dledbackend.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String secret;
    private long expirationMinutes;
    private String cookieName;
    private boolean cookieSecure;
}
