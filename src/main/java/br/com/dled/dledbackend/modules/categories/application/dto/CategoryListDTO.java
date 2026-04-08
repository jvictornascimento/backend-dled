package br.com.dled.dledbackend.modules.categories.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
@Builder
public record CategoryListDTO(
        @Schema(description = "List of root categories with their subcategories")
        List<CategoryTreeDTO> categories
) {
}
