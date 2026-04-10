package br.com.dled.dledbackend.modules.orders.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderProductDto(
        @Schema(description = "Product ID", example = "1")
        Long id,
        @Schema(description = "Product name", example = "Driver 24W")
        String name,
        @Schema(description = "Product status in English for frontend translation", example = "AVAILABLE")
        String status
) {
}
