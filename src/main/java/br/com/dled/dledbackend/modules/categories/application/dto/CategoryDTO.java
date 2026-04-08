package br.com.dled.dledbackend.modules.categories.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryDTO(

        @Schema(description = "Unique identifier of the category", example = "1")
        Long id,

        @Schema(description = "Name of the category", example = "LED Strips")
        String name,

        @Schema(description = "Image URL of the category")
        String imgUrl,

        @Schema(description = "Indicates if the category is active", example = "true")
        boolean active,

        @Schema(description = "Parent category ID. Null if this is a root category")
        Long parentId
) {
}
