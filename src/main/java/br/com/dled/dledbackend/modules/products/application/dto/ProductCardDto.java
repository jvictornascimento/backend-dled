package br.com.dled.dledbackend.modules.products.application.dto;

import br.com.dled.dledbackend.modules.categories.application.dto.CategoryForProductDTO;
import br.com.dled.dledbackend.modules.products.domain.ProductStatus;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Set;

public record ProductCardDto(
        @Parameter(description = "Product ID")
        Long id,
        @Parameter(description = "Product name")
        String name,
        @Parameter(description = "Product price")
        Double price,
        @Parameter(description = "Product image URl")
        String iconUrl,
        @Parameter(description = "Product Id this Russo")
        int codigoRusso,
        @Parameter(description = "Product Id this Mali")
        int codigoMali,
        @Parameter(description = "Availability status", example = "AVAILABLE")
        ProductStatus status,
        @Parameter(description = "Categories this product")
        Set<CategoryForProductDTO > categories

) {
}
