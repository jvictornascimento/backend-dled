package br.com.dled.dledbackend.modules.categories.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CategoryForProductDTO(

        @Schema(description = "Unique identifier of the category", example = "1")
        Long id,

        @Schema(description = "Name of the category", example = "LED Strips")
        String name
) {
}
