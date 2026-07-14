package br.com.dled.dledbackend.modules.users.application.dto;

import br.com.dled.dledbackend.modules.users.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserDto(
        @Schema(description = "User ID", example = "1")
        Long id,
        @Schema(description = "Full user name", example = "Administrador do Sistema")
        String fullName,
        @Schema(description = "Unique username used to log in", example = "admin")
        String username,
        @Schema(description = "Unique email address", example = "admin@dled.com")
        String email,
        @Schema(description = "Phone number", example = "11999999999")
        String phone,
        @Schema(description = "Company name stored as plain text. It is not related to the Company entity.", example = "Loja Centro")
        String companyName,
        @Schema(description = "User role", example = "ADMIN")
        UserRole role,
        @Schema(description = "Indicates whether the user is active", example = "true")
        boolean active,
        @Schema(description = "Creation date and time")
        LocalDateTime createdAt,
        @Schema(description = "Last update date and time")
        LocalDateTime updatedAt
) {
}
