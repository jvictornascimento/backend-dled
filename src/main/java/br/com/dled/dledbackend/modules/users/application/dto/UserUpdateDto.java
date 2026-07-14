package br.com.dled.dledbackend.modules.users.application.dto;

import br.com.dled.dledbackend.modules.users.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserUpdateDto(
        @Schema(description = "Full user name", example = "Administrador do Sistema")
        @NotBlank
        String fullName,
        @Schema(description = "Unique username", example = "admin")
        @NotBlank
        String username,
        @Schema(description = "Unique email address", example = "admin@dled.com")
        @Email
        @NotBlank
        String email,
        @Schema(description = "Phone number", example = "11999999999")
        @NotBlank
        String phone,
        @Schema(description = "Company name stored as plain text and unrelated to the Company entity", example = "Loja Centro")
        @NotBlank
        String companyName,
        @Schema(description = "Optional password update. When provided it must follow the password policy.", example = "Admin@123")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,20}$",
                message = "Password must be 8 to 20 characters long and contain uppercase, lowercase, number and special character."
        )
        String password,
        @Schema(description = "User role", example = "USER")
        @NotNull
        UserRole role,
        @Schema(description = "Indicates whether the user is active", example = "true")
        @NotNull
        Boolean active
) {
}
