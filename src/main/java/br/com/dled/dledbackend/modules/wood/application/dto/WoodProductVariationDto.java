package br.com.dled.dledbackend.modules.wood.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record WoodProductVariationDto(
        @Schema(description = "Variation ID", example = "1")
        Long id,
        @Schema(description = "Variation color", example = "Natural")
        String color,
        @Schema(description = "SKU", example = "123456")
        Long sku,
        @Schema(description = "EAN", example = "7891234567890")
        Long ean,
        @Schema(description = "Image URLs for this variation")
        List<String> listImgs,
        @Schema(description = "Custom label image URL stored in Cloudinary")
        String labelImageUrl,
        @Schema(description = "Cloudinary public ID for custom label image")
        String labelImagePublicId,
        @Schema(description = "Whether the variation is active", example = "true")
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
