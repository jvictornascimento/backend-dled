package br.com.dled.dledbackend.modules.categories.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpsertDTO(
        @Schema(description = "Display name of the category", example = "Fitas LED")
        @NotBlank
        String name,

        @Schema(
                description = "Public image URL consumed by the frontend to render the category",
                example = "https://cdn.exemplo.com/categories/fitas-led.png"
        )
        String imgUrl,

        @Schema(description = "Indicates whether the category is active", example = "true")
        @NotNull
        Boolean active,

        @Schema(description = "Parent category ID. Use null for a root category", example = "1")
        Long parentId
) {
}
