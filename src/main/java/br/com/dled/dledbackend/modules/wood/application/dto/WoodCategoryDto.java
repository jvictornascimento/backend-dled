package br.com.dled.dledbackend.modules.wood.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WoodCategoryDto(
        @Schema(description = "Category ID", example = "1")
        Long id,
        @Schema(description = "Category name", example = "MDF")
        String name,
        @Schema(description = "Category image URL")
        String imgCategoryUrl,
        @Schema(description = "Parent category ID. Null means root category.")
        Long parentId,
        @Schema(description = "Whether the category is active", example = "true")
        boolean active
) {
}
