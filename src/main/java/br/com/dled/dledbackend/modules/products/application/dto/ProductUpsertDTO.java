package br.com.dled.dledbackend.modules.products.application.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import br.com.dled.dledbackend.modules.products.domain.ProductStatus;

import java.util.Set;

public record ProductUpsertDTO(
        @Schema(description = "Commercial product name", example = "Driver 60W")
        @NotBlank
        String name,

        @Schema(description = "Russo product code", example = "321")
        int codigoRusso,

        @Schema(description = "Mali product code", example = "654")
        int codigoMali,

        @ArraySchema(schema = @Schema(description = "Category ID linked to the product", example = "1"))
        @NotEmpty
        Set<Long> categoryIds,

        @Schema(description = "Availability status for the product", example = "AVAILABLE")
        @NotNull
        ProductStatus status,

        @Schema(description = "Detailed product description", example = "Driver para fitas LED")
        String descricao,

        @Schema(description = "Usage restrictions visible to clients", example = "Do not use in outdoor areas.")
        String restricoesDeUso,

        @Schema(description = "Usage recommendations visible to clients", example = "Use with stabilized power supply.")
        String recomendacoesDeUso,

        @Schema(description = "Special observations visible to clients", example = "Special lot for premium projects.")
        String observacoesEspeciais,

        @Schema(description = "Ingress protection rating", example = "65")
        int ip,

        @Schema(description = "Electrical current in amperes", example = "5")
        int amper,

        @Schema(description = "Power in watts", example = "60")
        int watts,

        @Schema(description = "GTIN code", example = "7891234567000")
        long gtin,

        @Schema(description = "Voltage in volts", example = "24")
        int volt,

        @Schema(
                description = "Public image URL consumed by the frontend to render the product detail",
                example = "https://cdn.exemplo.com/products/driver-60w.png"
        )
        String imgUrl,

        @Schema(description = "Product price", example = "149.90")
        Double price,

        @Schema(
                description = "Public icon URL consumed by the frontend to render product cards",
                example = "https://cdn.exemplo.com/products/driver-60w-icon.png"
        )
        String iconUrl,

        @Schema(description = "Color temperature", example = "3000K")
        String temperaturaDeCor,

        @Schema(description = "LED quantity per meter", example = "120")
        int ledsPorMetro,

        @Schema(description = "LED technology used by the product", example = "SMD")
        String tipoLed,

        @Schema(description = "Luminous flux", example = "2400lm")
        String fluxoLuminoso,

        @Schema(description = "Color rendering index", example = "90")
        String indiceDeReproducaoDeCor,

        @Schema(description = "Quantity per roll", example = "5")
        int quantidePorRolo,

        @Schema(description = "Cut section in millimeters", example = "10")
        int sessaoDeCorte,

        @Schema(description = "Thickness in millimeters", example = "2")
        int espessura,

        @Schema(description = "Indicates whether the product is armored", example = "true")
        boolean blindada,

        @Schema(description = "Product dimensions", example = "5m")
        String dimensao,

        @Schema(description = "Indicates whether the product is active", example = "true")
        @NotNull
        Boolean active
) {
}
