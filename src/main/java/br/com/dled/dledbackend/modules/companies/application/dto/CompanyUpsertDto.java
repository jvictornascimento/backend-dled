package br.com.dled.dledbackend.modules.companies.application.dto;

import br.com.dled.dledbackend.modules.companies.domain.CompanyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanyUpsertDto(
        @Schema(description = "Short trade name used in the system", example = "DLED")
        @NotBlank
        String shortName,
        @Schema(description = "Full legal or business name", example = "DLED Lighting Industry")
        @NotBlank
        String fullName,
        @Schema(description = "Company type. Use OWN for internal companies and SUPPLIER for vendors.", example = "SUPPLIER")
        @NotNull
        CompanyType type
) {
}
