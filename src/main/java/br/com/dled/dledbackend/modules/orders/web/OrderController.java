package br.com.dled.dledbackend.modules.orders.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.orders.application.IOrderService;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderDto;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderUpsertDto;
import br.com.dled.dledbackend.modules.orders.application.dto.PrintLabelProductDTO;
import br.com.dled.dledbackend.modules.orders.application.dto.PrintLabelProductRequestDto;
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
@RequestMapping("${api.prefix}/orders")
@Tag(name = "Order", description = "Order Controller")
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "authCookie")
public class OrderController {
    private final IOrderService service;

    @GetMapping
    @Operation(summary = "Return list with all orders", description = "Returns all purchase orders with purchase date, lot, related company and the list of products included in each order.")
    @ApiResponse(responseCode = "200", description = "Orders returned successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class)))
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Return order by ID", description = "Returns one order by ID with purchase date, lot, related company, selected products and creation date.")
    @ApiResponse(responseCode = "200", description = "Order found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class)))
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<OrderDto> getOrderById(
            @Parameter(required = true, description = "Order ID", example = "1")
            @PathVariable Long orderId) {
        return ResponseEntity.ok(service.getById(orderId));
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Creates a new purchase order. The payload must include the purchase date, lot, one related company and a list of existing product IDs.")
    @ApiResponse(responseCode = "201", description = "Order created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class)))
    @ApiResponse(responseCode = "404", description = "Company or product not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderUpsertDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input));
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update order", description = "Updates an existing purchase order. The payload replaces purchase date, lot, related company and the full list of products linked to the order.")
    @ApiResponse(responseCode = "200", description = "Order updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class)))
    @ApiResponse(responseCode = "404", description = "Order, company or product not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<OrderDto> updateOrder(
            @Parameter(required = true, description = "Order ID", example = "1")
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpsertDto input) {
        return ResponseEntity.ok(service.update(orderId, input));
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete order", description = "Deletes a purchase order by ID.")
    @ApiResponse(responseCode = "204", description = "Order deleted")
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<Void> deleteOrder(
            @Parameter(required = true, description = "Order ID", example = "1")
            @PathVariable Long orderId) {
        service.delete(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/labels/products")
    @Operation(summary = "Build product label data", description = "Loads non-operational product data for label printing using order ID, lot and product ID.")
    @ApiResponse(responseCode = "200", description = "Product label data returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrintLabelProductDTO.class)))
    @ApiResponse(responseCode = "404", description = "Order, lot or product not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<PrintLabelProductDTO> buildProductLabel(@Valid @RequestBody PrintLabelProductRequestDto input) {
        return ResponseEntity.ok(service.buildProductLabel(input));
    }
}
