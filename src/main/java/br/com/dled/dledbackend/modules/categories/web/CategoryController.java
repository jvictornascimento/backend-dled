package br.com.dled.dledbackend.modules.categories.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.categories.application.ICategoryService;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategorySimpleDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryTreeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategorySimpleDTO.class)
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
    public ResponseEntity<List<CategorySimpleDTO>> getAllRootCategory(){
        return ResponseEntity.ok(service.getListRootCategory());
    }
    @GetMapping("/{categoryID}")
    @Operation(summary = "Return Categories")
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
}
