package br.com.dled.dledbackend.modules.products.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.products.application.IProductService;
import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductDetailDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductUpsertDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    @Operation(
            summary = "Create product",
            description = "Creates a new product. The imgUrl and iconUrl fields must contain public URLs that the frontend can interpret and render for the client."
    )
    @ApiResponse(responseCode = "201", description = "Product created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDetailDto.class)))
    @ApiResponse(responseCode = "404", description = "Product category not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<ProductDetailDto> createProduct(@Valid @RequestBody ProductUpsertDTO input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input));
    }

    @PutMapping("/{productId}")
    @Operation(
            summary = "Update product",
            description = "Updates an existing product. The imgUrl and iconUrl fields must contain public URLs that the frontend can interpret and render for the client."
    )
    @ApiResponse(responseCode = "200", description = "Product updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDetailDto.class)))
    @ApiResponse(responseCode = "404", description = "Product or category not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<ProductDetailDto> updateProduct(
            @Parameter(required = true, description = "Product ID", example = "1")
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpsertDTO input) {
        return ResponseEntity.ok(service.update(productId, input));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product", description = "Deletes a product by ID.")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<Void> deleteProduct(
            @Parameter(required = true, description = "Product ID", example = "1")
            @PathVariable Long productId) {
        service.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
