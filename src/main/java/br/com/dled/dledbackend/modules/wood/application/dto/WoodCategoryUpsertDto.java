package br.com.dled.dledbackend.modules.wood.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WoodCategoryUpsertDto(
        @Schema(description = "Category name", example = "MDF")
        @NotBlank
        String name,
        @Schema(description = "Category image URL")
        String imgCategoryUrl,
        @Schema(description = "Parent category ID. Null means root category.")
        Long parentId,
        @Schema(description = "Whether the category is active", example = "true")
        @NotNull
        Boolean active
) {
}
