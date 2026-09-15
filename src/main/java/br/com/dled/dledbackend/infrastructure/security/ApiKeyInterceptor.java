package br.com.dled.dledbackend.infrastructure.security;

import br.com.dled.dledbackend.infrastructure.config.ApiKeyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.INVALID_API_KEY;

@Slf4j
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {
    private final ApiKeyProperties apiKeyProperties;

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
                             jakarta.servlet.http.HttpServletResponse response,
                             Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod()) || !HttpMethod.GET.matches(request.getMethod())) {
            return true;
        }

        String apiKey = request.getHeader(apiKeyProperties.getHeaderName());
        if (apiKey == null || !matchesConfiguredKey(apiKey)) {
            throw new InvalidApiKeyException(INVALID_API_KEY.getMassage());
        }
        return true;
    }

    private boolean matchesConfiguredKey(String apiKey) {
        byte[] presentedHash = hash(apiKey);
        boolean valid = false;
        String matchedKeyId = null;

        for (ApiKeyProperties.Key configuredKey : apiKeyProperties.getKeys()) {
            if (configuredKey.getHash() == null || configuredKey.getHash().isBlank()) {
                continue;
            }

            byte[] configuredHash = decodeHash(configuredKey);
            boolean matches = MessageDigest.isEqual(presentedHash, configuredHash);
            valid = valid || matches;
            if (matches) {
                matchedKeyId = configuredKey.getId();
            }
        }

        if (valid) {
            log.debug("Accepted public API key '{}'", matchedKeyId);
        }
        return valid;
    }

    private byte[] hash(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Could not hash public API key.", exception);
        }
    }

    private byte[] decodeHash(ApiKeyProperties.Key configuredKey) {
        try {
            return Base64.getDecoder().decode(configuredKey.getHash().trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Invalid public API key hash configured for key '%s'."
                    .formatted(configuredKey.getId()), exception);
        }
    }
}
