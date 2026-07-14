package br.com.dled.dledbackend.modules.orders.application.dto;

import br.com.dled.dledbackend.modules.products.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record PrintLabelProductDTO(
        @Schema(description = "Order ID", example = "1")
        Long orderId,

        @Schema(description = "Batch or lot identifier used for the label", example = "L-2026-001")
        String lot,

        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(description = "Commercial product name", example = "Driver 60W")
        String name,

        @Schema(description = "Product description", example = "Driver para fitas LED")
        String descricao,

        @Schema(description = "Russo product code", example = "321")
        int codigoRusso,

        @Schema(description = "Mali product code", example = "654")
        int codigoMali,

        @Schema(description = "GTIN code", example = "7891234567000")
        long gtin,

        @Schema(description = "Product price", example = "149.90")
        Double price,

        @Schema(description = "Availability status", example = "AVAILABLE")
        ProductStatus status,

        @Schema(description = "Power in watts", example = "60")
        int watts,

        @Schema(description = "Voltage in volts", example = "24")
        int volt,

        @Schema(description = "Electrical current in amperes", example = "5")
        int amper,

        @Schema(description = "Ingress protection rating", example = "65")
        int ip,

        @Schema(description = "Color temperature", example = "3000K")
        String temperaturaDeCor,

        @Schema(description = "Product dimensions", example = "5m")
        String dimensao
) {
}
