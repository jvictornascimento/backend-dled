package br.com.dled.dledbackend.modules.wood.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.wood.application.IWoodProductService;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductFullDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductListDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductUpsertDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductVariationDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductVariationUpsertDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/wood/products")
@Tag(name = "Wood Product", description = "Wood product controller")
public class WoodProductController {
    private final IWoodProductService service;

    @GetMapping
    @Operation(summary = "List active wood products")
    @SecurityRequirement(name = "apiKeyAuth")
    public ResponseEntity<List<WoodProductListDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get wood product by ID")
    @SecurityRequirement(name = "apiKeyAuth")
    @ApiResponse(responseCode = "404", description = "Wood product not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<WoodProductFullDto> getById(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getById(productId));
    }

    @PostMapping
    @Operation(summary = "Create wood product")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<WoodProductFullDto> create(@Valid @RequestBody WoodProductUpsertDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input));
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update wood product")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<WoodProductFullDto> update(@PathVariable Long productId, @Valid @RequestBody WoodProductUpsertDto input) {
        return ResponseEntity.ok(service.update(productId, input));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete wood product")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        service.delete(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload main image for wood product")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<WoodProductFullDto> uploadProductImage(
            @PathVariable Long productId,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(service.uploadProductImage(productId, file));
    }

    @PostMapping("/{productId}/variations")
    @Operation(summary = "Create wood product variation")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<WoodProductVariationDto> createVariation(
            @PathVariable Long productId,
            @Valid @RequestBody WoodProductVariationUpsertDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createVariation(productId, input));
    }

    @PutMapping("/{productId}/variations/{variationId}")
    @Operation(summary = "Update wood product variation")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<WoodProductVariationDto> updateVariation(
            @PathVariable Long productId,
            @PathVariable Long variationId,
            @Valid @RequestBody WoodProductVariationUpsertDto input) {
        return ResponseEntity.ok(service.updateVariation(productId, variationId, input));
    }

    @DeleteMapping("/{productId}/variations/{variationId}")
    @Operation(summary = "Delete wood product variation")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<Void> deleteVariation(@PathVariable Long productId, @PathVariable Long variationId) {
        service.deleteVariation(productId, variationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{productId}/variations/{variationId}/label", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload custom label image for wood variation")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<WoodProductVariationDto> uploadVariationLabel(
            @PathVariable Long productId,
            @PathVariable Long variationId,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(service.uploadVariationLabel(productId, variationId, file));
    }
}
