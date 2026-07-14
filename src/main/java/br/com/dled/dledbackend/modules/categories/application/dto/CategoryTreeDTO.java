package br.com.dled.dledbackend.modules.categories.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CategoryTreeDTO(

    @Schema
            (description = "Unique identifier of the category", example = "1")
    Long id,

    @Schema(description = "Name of the category", example = "Lighting")
    String name,

    @Schema(description = "Image URL of the category")
    String imgUrl,

    @Schema(description = "Indicates if the category is active", example = "true")
    boolean active,

    @Schema(description = "List of subcategories")
    List<CategoryTreeDTO> children
) {}