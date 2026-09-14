package br.com.dled.dledbackend.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "security.initial-admin")
public class InitialAdminBootstrapProperties {
    private boolean enabled = true;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String companyName;
    private String password;
}
