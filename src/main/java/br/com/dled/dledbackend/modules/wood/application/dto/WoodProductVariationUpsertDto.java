package br.com.dled.dledbackend.modules.wood.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WoodProductVariationUpsertDto(
        @Schema(description = "Variation color", example = "Natural")
        String color,
        @Schema(description = "SKU", example = "123456")
        Long sku,
        @Schema(description = "EAN", example = "7891234567890")
        Long ean,
        @Schema(description = "Image URLs for this variation")
        List<String> listImgs,
        @Schema(description = "Whether the variation is active", example = "true")
        @NotNull
        Boolean active
) {
}
