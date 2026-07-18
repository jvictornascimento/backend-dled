package br.com.dled.dledbackend.modules.wood.application.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record WoodProductUpsertDto(
        @Schema(description = "Product name", example = "Caixa MDF 30x40")
        @NotBlank
        String name,
        @Schema(description = "Product description")
        String description,
        @Schema(description = "Product image URL")
        String imgUrl,
        @Schema(description = "Packaging code used by employees", example = "CX-PADRAO-02")
        String caixa,
        @Schema(description = "Product price", example = "49.90")
        Double price,
        Double widthMm,
        Double heightMm,
        Double lengthMm,
        Double weightKg,
        @ArraySchema(schema = @Schema(description = "Wood category ID", example = "1"))
        Set<Long> categoryIds,
        @Schema(description = "Whether the product is active", example = "true")
        @NotNull
        Boolean active
) {
}
