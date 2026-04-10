package br.com.dled.dledbackend.modules.companies.application.dto;

import br.com.dled.dledbackend.modules.companies.domain.CompanyType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CompanyDto(
        @Schema(description = "Company ID", example = "1")
        Long id,
        @Schema(description = "Short trade name used in the system", example = "DLED")
        String shortName,
        @Schema(description = "Full legal or business name", example = "DLED Lighting Industry")
        String fullName,
        @Schema(description = "Company type. Use OWN for internal companies and SUPPLIER for vendors.", example = "OWN")
        CompanyType type,
        @Schema(description = "Creation date and time")
        LocalDateTime createdAt
) {
}
