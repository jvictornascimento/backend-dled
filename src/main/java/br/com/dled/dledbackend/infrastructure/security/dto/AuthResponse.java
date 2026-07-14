package br.com.dled.dledbackend.infrastructure.security.dto;

import br.com.dled.dledbackend.modules.users.application.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(description = "JWT token that can also be sent as Bearer token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        @Schema(description = "Authenticated user data")
        UserDto user
) {
}
