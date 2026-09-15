package br.com.dled.dledbackend.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "security.api-key")
public class ApiKeyProperties {
    private String headerName;
    private List<Key> keys = new ArrayList<>();

    @Data
    public static class Key {
        private String id;
        private String hash;
    }
}
