package br.com.dled.dledbackend.modules.orders.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record OrderUpsertDto(
        @Schema(description = "Purchase date of the order", example = "2026-04-10")
        @NotNull
        LocalDate purchaseDate,
        @Schema(description = "Batch or lot identifier used for the purchase", example = "L-2026-001")
        @NotBlank
        String lot,
        @Schema(description = "IDs of the products included in the order", example = "[1,2]")
        @NotEmpty
        Set<Long> productIds,
        @Schema(description = "Company ID related to the order", example = "1")
        @NotNull
        Long companyId
) {
}
