package br.com.dled.dledbackend.modules.orders.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        @Schema(description = "Order ID", example = "1")
        Long id,
        @Schema(description = "Purchase date of the order", example = "2026-04-10")
        LocalDate purchaseDate,
        @Schema(description = "Batch or lot identifier used for the purchase", example = "L-2026-001")
        String lot,
        @Schema(description = "Products included in the order")
        List<OrderProductDto> products,
        @Schema(description = "Company related to the order")
        OrderCompanyDto company,
        @Schema(description = "Creation date and time")
        LocalDateTime createdAt
) {
}
