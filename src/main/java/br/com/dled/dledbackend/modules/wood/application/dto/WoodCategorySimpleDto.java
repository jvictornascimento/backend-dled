package br.com.dled.dledbackend.modules.wood.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WoodCategorySimpleDto(
        @Schema(description = "Category ID", example = "1")
        Long id,
        @Schema(description = "Category name", example = "MDF")
        String name
) {
}
