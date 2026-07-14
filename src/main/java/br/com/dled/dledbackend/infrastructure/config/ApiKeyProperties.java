package br.com.dled.dledbackend.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "security.api-key")
public class ApiKeyProperties {
    private String headerName;
    private String value;
}
