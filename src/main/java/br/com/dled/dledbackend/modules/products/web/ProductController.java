package br.com.dled.dledbackend.modules.products.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.products.application.IProductService;
import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
@Tag(name = "Product", description = "Product Controller")
public class ProductController {
    private final IProductService service;

    @GetMapping
    @Operation(summary = "Return list with all products")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductCardDto.class)
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StandardError.class)
            )
    )
    public ResponseEntity<List<ProductCardDto>> getAllProducts(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Return product details by ID")
    @ApiResponse(
            responseCode = "200",
            description = "Product found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductDetailDto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StandardError.class)
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StandardError.class)
            )
    )
    public ResponseEntity<ProductDetailDto> getProductById(
            @Parameter(required = true, description = "Product ID", example = "1")
            @PathVariable Long productId) {
        return ResponseEntity.ok(service.getById(productId));
    }
}
