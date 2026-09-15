package br.com.dled.dledbackend.infrastructure.security;

import br.com.dled.dledbackend.infrastructure.config.ApiKeyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiKeyInterceptorTest {

    private ApiKeyInterceptor interceptor;
    private ApiKeyProperties apiKeyProperties;

    @BeforeEach
    void setUp() {
        apiKeyProperties = new ApiKeyProperties();
        apiKeyProperties.setHeaderName("X-API-Key");
        apiKeyProperties.setKeys(List.of(apiKey("primary", "TIBjYrYT90lqvyhBRu/THakOSxYWn+ABhByhcpD0J8Q=")));
        interceptor = new ApiKeyInterceptor(apiKeyProperties);
    }

    @Test
    void shouldAllowRequestWhenApiKeyIsValid() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/products");
        request.addHeader("X-API-Key", "test-api-key");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));

        assertTrue(result);
    }

    @Test
    void shouldAllowRequestWhenRotatedApiKeyIsConfigured() {
        apiKeyProperties.setKeys(List.of(
                apiKey("current", "TIBjYrYT90lqvyhBRu/THakOSxYWn+ABhByhcpD0J8Q="),
                apiKey("next", "iy7ml09tRjxJP2vM+QHXly79eL/A6jDuCb/Su03ZT7E=")
        ));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/products");
        request.addHeader("X-API-Key", "rotated-api-key");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));

        assertTrue(result);
    }

    @Test
    void shouldThrowWhenApiKeyIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/products");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(InvalidApiKeyException.class, () -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void shouldThrowWhenApiKeyIsInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/products");
        request.addHeader("X-API-Key", "wrong-key");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(InvalidApiKeyException.class, () -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void shouldAllowOptionsRequestWithoutApiKey() {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/v1/products");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));

        assertTrue(result);
    }

    private ApiKeyProperties.Key apiKey(String id, String hash) {
        ApiKeyProperties.Key key = new ApiKeyProperties.Key();
        key.setId(id);
        key.setHash(hash);
        return key;
    }
}
