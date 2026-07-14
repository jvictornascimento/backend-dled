package br.com.dled.dledbackend.modules.wood.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.wood.application.IWoodCategoryService;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodCategoryDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodCategoryUpsertDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("${api.prefix}/wood/categories")
@Tag(name = "Wood Category", description = "Wood category controller")
public class WoodCategoryController {
    private final IWoodCategoryService service;

    @GetMapping
    @Operation(summary = "List active wood categories")
    @SecurityRequirement(name = "apiKeyAuth")
    public ResponseEntity<List<WoodCategoryDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/root")
    @Operation(summary = "List active root wood categories")
    @SecurityRequirement(name = "apiKeyAuth")
    public ResponseEntity<List<WoodCategoryDto>> getRootCategories() {
        return ResponseEntity.ok(service.getRootCategories());
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get wood category by ID")
    @SecurityRequirement(name = "apiKeyAuth")
    @ApiResponse(responseCode = "404", description = "Wood category not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<WoodCategoryDto> getById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(service.getById(categoryId));
    }

    @PostMapping
    @Operation(summary = "Create wood category")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<WoodCategoryDto> create(@Valid @RequestBody WoodCategoryUpsertDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input));
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Update wood category")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<WoodCategoryDto> update(@PathVariable Long categoryId, @Valid @RequestBody WoodCategoryUpsertDto input) {
        return ResponseEntity.ok(service.update(categoryId, input));
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete wood category")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        service.delete(categoryId);
        return ResponseEntity.noContent().build();
    }
}
