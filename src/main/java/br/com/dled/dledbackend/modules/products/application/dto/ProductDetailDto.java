package br.com.dled.dledbackend.modules.products.application.dto;

import br.com.dled.dledbackend.modules.categories.application.dto.CategoryForProductDTO;
import br.com.dled.dledbackend.modules.products.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ProductDetailDto(
        @Schema(description = "Product ID", example = "1")
        Long id,
        @Schema(description = "Product name", example = "Fita LED 12V")
        String name,
        @Schema(description = "Product Russo code", example = "100")
        int codigoRusso,
        @Schema(description = "Product Mali code", example = "200")
        int codigoMali,
        @Schema(description = "Categories associated with the product")
        Set<CategoryForProductDTO> categories,
        @Schema(description = "Availability status", example = "AVAILABLE")
        ProductStatus status,
        @Schema(description = "Product description")
        String descricao,
        @Schema(description = "Usage restrictions for the product", example = "Do not use in outdoor areas.")
        String restricoesDeUso,
        @Schema(description = "Usage recommendations for the product", example = "Recommended for indoor lighting projects.")
        String recomendacoesDeUso,
        @Schema(description = "Special observations visible to clients", example = "Special lot with 12 months warranty.")
        String observacoesEspeciais,
        @Schema(description = "Product IP", example = "65")
        int ip,
        @Schema(description = "Product amperage", example = "5")
        int amper,
        @Schema(description = "Product watts", example = "24")
        int watts,
        @Schema(description = "Product GTIN", example = "7891234567890")
        long gtin,
        @Schema(description = "Product voltage", example = "12")
        int volt,
        @Schema(description = "Product image URL")
        String imgUrl,
        @Schema(description = "Product price", example = "199.90")
        Double price,
        @Schema(description = "Product icon URL")
        String iconUrl,
        @Schema(description = "Color temperature")
        String temperaturaDeCor,
        @Schema(description = "LEDs per meter", example = "60")
        int ledsPorMetro,
        @Schema(description = "LED type")
        String tipoLed,
        @Schema(description = "Luminous flux")
        String fluxoLuminoso,
        @Schema(description = "Color rendering index")
        String indiceDeReproducaoDeCor,
        @Schema(description = "Quantity per roll", example = "5")
        int quantidePorRolo,
        @Schema(description = "Cut section", example = "10")
        int sessaoDeCorte,
        @Schema(description = "Product thickness", example = "2")
        int espessura,
        @Schema(description = "Indicates if the product is armored", example = "true")
        boolean blindada,
        @Schema(description = "Product dimension")
        String dimensao,
        @Schema(description = "Mini gallery with up to 5 product images")
        List<ProductGalleryImageDto> galleryImages,
        @Schema(description = "Creation date and time")
        LocalDateTime createdAt,
        @Schema(description = "Indicates if the product is active", example = "true")
        boolean active
) {
}
