package br.com.dled.dledbackend.modules.products.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductGalleryImageDto(
        @Schema(description = "Gallery image ID", example = "1")
        Long id,
        @Schema(description = "Public image URL returned by Cloudinary", example = "https://res.cloudinary.com/demo/image/upload/v1/products/1/gallery/image.png")
        String imageUrl
) {
}
