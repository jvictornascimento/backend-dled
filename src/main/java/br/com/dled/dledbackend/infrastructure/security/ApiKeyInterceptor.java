package br.com.dled.dledbackend.infrastructure.security;

import br.com.dled.dledbackend.infrastructure.config.ApiKeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.INVALID_API_KEY;

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
        if (apiKey == null || !apiKey.equals(apiKeyProperties.getValue())) {
            throw new InvalidApiKeyException(INVALID_API_KEY.getMassage());
        }
        return true;
    }
}
