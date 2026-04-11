package br.com.dled.dledbackend.modules.categories.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.categories.application.ICategoryService;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryForFilterDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryTreeDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryUpsertDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("${api.prefix}/categories")
@Tag(name = "Category", description = "Category Controller")
public class CategoryController {
    private final ICategoryService service;

    @GetMapping("/tree")
    @Operation(summary = "Get all categories in tree format")
    @SecurityRequirement(name = "apiKeyAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryTreeDTO.class)
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
    public ResponseEntity<List<CategoryTreeDTO>> getAllCategory(){
        return ResponseEntity.ok(service.getAllCategory());
    }
    @GetMapping("/root")
    @Operation(summary = "Get all root categories")
    @SecurityRequirement(name = "apiKeyAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryForFilterDTO.class)
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
    public ResponseEntity<List<CategoryForFilterDTO>> getAllRootCategory(){
        return ResponseEntity.ok(service.getListRootCategory());
    }
    @GetMapping("/{categoryID}")
    @Operation(summary = "Return Categories")
    @SecurityRequirement(name = "apiKeyAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Category found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = StandardError.class)
    ))
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StandardError.class)
            )
    )
    public ResponseEntity<CategoryDTO> getCategoryByID(
            @Parameter(required = true,description = "Category ID", example = "1")
            @PathVariable Long categoryID){
        return ResponseEntity.ok(service.getCategoryById(categoryID));
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Creates a new category. The imgUrl field must contain a public URL that the frontend can render directly.")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    @ApiResponse(responseCode = "201", description = "Category created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class)))
    @ApiResponse(responseCode = "404", description = "Parent category not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryUpsertDTO input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input));
    }

    @PutMapping("/{categoryID}")
    @Operation(summary = "Update category", description = "Updates an existing category. The imgUrl field must contain a public URL that the frontend can render directly.")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    @ApiResponse(responseCode = "200", description = "Category updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class)))
    @ApiResponse(responseCode = "404", description = "Category not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(required = true, description = "Category ID", example = "1")
            @PathVariable Long categoryID,
            @Valid @RequestBody CategoryUpsertDTO input) {
        return ResponseEntity.ok(service.update(categoryID, input));
    }

    @DeleteMapping("/{categoryID}")
    @Operation(summary = "Delete category", description = "Deletes a category by ID.")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    @ApiResponse(responseCode = "204", description = "Category deleted")
    @ApiResponse(responseCode = "404", description = "Category not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<Void> deleteCategory(
            @Parameter(required = true, description = "Category ID", example = "1")
            @PathVariable Long categoryID) {
        service.delete(categoryID);
        return ResponseEntity.noContent().build();
    }
}
