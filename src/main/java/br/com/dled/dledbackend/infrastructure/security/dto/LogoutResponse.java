package br.com.dled.dledbackend.infrastructure.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LogoutResponse(
        @Schema(description = "Logout result message", example = "Logout successful")
        String message
) {
}
