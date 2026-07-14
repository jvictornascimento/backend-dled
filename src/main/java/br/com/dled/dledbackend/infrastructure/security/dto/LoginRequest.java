package br.com.dled.dledbackend.infrastructure.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "Username used to authenticate", example = "user")
        @NotBlank
        String username,
        @Schema(description = "Plain password", example = "123456")
        @NotBlank
        String password
) {
}
