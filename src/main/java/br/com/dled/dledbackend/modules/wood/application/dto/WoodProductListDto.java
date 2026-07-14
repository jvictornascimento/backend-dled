package br.com.dled.dledbackend.modules.wood.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WoodProductListDto(
        @Schema(description = "Product ID", example = "1")
        Long id,
        @Schema(description = "Product image URL")
        String imgUrl,
        @Schema(description = "Product name", example = "Caixa MDF 30x40")
        String name,
        @Schema(description = "Packaging code used by employees", example = "CX-PADRAO-02")
        String caixa,
        @Schema(description = "Whether the product is active", example = "true")
        boolean active
) {
}
