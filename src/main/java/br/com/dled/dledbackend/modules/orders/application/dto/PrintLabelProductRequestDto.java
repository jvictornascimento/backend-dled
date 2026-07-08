package br.com.dled.dledbackend.modules.orders.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrintLabelProductRequestDto(
        @Schema(description = "Order ID used to load the product label data", example = "1")
        @NotNull
        Long orderId,

        @Schema(description = "Batch or lot identifier related to the order", example = "L-2026-001")
        @NotBlank
        String lot,

        @Schema(description = "Product ID included in the order", example = "1")
        @NotNull
        Long productId
) {
}
