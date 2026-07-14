package br.com.dled.dledbackend.modules.apiKey.domain.infrastructure.security;


import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class ApiKeyProvider {

    public String generate(String client) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);

            String key = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(bytes);

            return "dled_" + client + "_" + key;

        } catch (Exception e) {
            throw new RuntimeException("Error generating API key", e);
        }
    }

    public String hash(String apiKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(apiKey.getBytes());
            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("Error hashing API key", e);
        }
    }
}
