package br.com.dled.dledbackend.modules.wood.application.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record WoodProductUpsertDto(
        @Schema(description = "Product name", example = "Caixa MDF 30x40")
        @NotBlank
        @Size(max = 255)
        String name,
        @Schema(description = "Product description")
        @Size(max = 4000)
        String description,
        @Schema(description = "Product image URL")
        @Size(max = 255)
        String imgUrl,
        @Schema(description = "Packaging code used by employees", example = "CX-PADRAO-02")
        @Size(max = 255)
        String caixa,
        @Schema(description = "Wood type", example = "MDF")
        @Size(max = 255)
        String woodType,
        @Schema(description = "Wood finish", example = "Natural")
        @Size(max = 255)
        String finish,
        @Schema(description = "Product price", example = "49.90")
        @PositiveOrZero
        Double price,
        @PositiveOrZero
        Double thicknessMm,
        @PositiveOrZero
        Double widthMm,
        @PositiveOrZero
        Double heightMm,
        @PositiveOrZero
        Double lengthMm,
        @PositiveOrZero
        Double weightKg,
        @ArraySchema(schema = @Schema(description = "Wood category ID", example = "1"))
        @Size(max = 50)
        Set<Long> categoryIds,
        @Schema(description = "Whether the product is active", example = "true")
        @NotNull
        Boolean active
) {
}
