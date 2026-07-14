package br.com.dled.dledbackend.modules.wood.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

public record WoodProductFullDto(
        @Schema(description = "Product ID", example = "1")
        Long id,
        @Schema(description = "Product image URL")
        String imgUrl,
        @Schema(description = "Product name", example = "Caixa MDF 30x40")
        String name,
        @Schema(description = "Product description")
        String description,
        @Schema(description = "Packaging code used by employees", example = "CX-PADRAO-02")
        String caixa,
        String woodType,
        String finish,
        Double thicknessMm,
        Double widthMm,
        Double heightMm,
        Double lengthMm,
        Double weightKg,
        Set<WoodCategorySimpleDto> categories,
        List<WoodProductVariationDto> variations,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
