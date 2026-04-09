package br.com.dled.dledbackend.infrastructure.config;

import br.com.dled.dledbackend.infrastructure.security.ApiKeyInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ApiKeyProperties.class)
public class WebConfig implements WebMvcConfigurer {
    @Value("${cors.origin}")
    private String corsOrigin;
    @Value("${api.prefix}")
    private String apiPrefix;
    private final ApiKeyInterceptor apiKeyInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(apiPrefix + "/**")
                .allowedOriginPatterns(corsOrigin)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyInterceptor)
                .addPathPatterns(apiPrefix + "/**");
    }
}
