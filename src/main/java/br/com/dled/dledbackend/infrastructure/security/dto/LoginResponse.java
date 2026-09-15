package br.com.dled.dledbackend.infrastructure.security.dto;

import br.com.dled.dledbackend.modules.users.application.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "Authenticated user data")
        UserDto user
) {
}
