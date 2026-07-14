package br.com.dled.dledbackend.modules.orders.application.dto;

import br.com.dled.dledbackend.modules.companies.domain.CompanyType;
import io.swagger.v3.oas.annotations.media.Schema;

public record OrderCompanyDto(
        @Schema(description = "Company ID", example = "1")
        Long id,
        @Schema(description = "Short trade name", example = "DLED")
        String shortName,
        @Schema(description = "Full company name", example = "DLED Lighting Industry")
        String fullName,
        @Schema(description = "Company type", example = "OWN")
        CompanyType type
) {
}
