package br.com.dled.dledbackend.modules.products.web;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
@Tag(name = "Product", description = "Product Controller")
public class ProductController {
    private final ProductServiceImpl service;

    @GetMapping
    @Operation(summary = "Return list with all products")
    @ApiResponse(responseCode = "200", description = "Successfully")
    public ResponseEntity<List<ProductCardDto>> getAllProducts(){
        return ResponseEntity.ok(service.getAll());
    }
}
