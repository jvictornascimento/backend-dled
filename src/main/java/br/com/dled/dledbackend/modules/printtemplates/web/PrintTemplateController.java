package br.com.dled.dledbackend.modules.printtemplates.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.printtemplates.application.IPrintTemplateService;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateCreateDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateListDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateUpdateDto;
import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplateUsageContext;
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
@RequestMapping("${api.prefix}/print-templates")
@Tag(name = "Print Template", description = "Print Template Controller")
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "authCookie")
public class PrintTemplateController {
    private final IPrintTemplateService service;

    @GetMapping
    @Operation(summary = "Return list with all print templates", description = "Returns all registered print templates without the full pdfme JSON payload.")
    @ApiResponse(responseCode = "200", description = "Print templates returned successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrintTemplateListDto.class)))
    public ResponseEntity<List<PrintTemplateListDto>> getAllPrintTemplates() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/context/{usageContext}")
    @Operation(summary = "Return active print templates by context", description = "Returns active print templates available for the requested usage context.")
    @ApiResponse(responseCode = "200", description = "Print templates returned successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrintTemplateListDto.class)))
    public ResponseEntity<List<PrintTemplateListDto>> getPrintTemplatesByContext(
            @Parameter(required = true, description = "Usage context", example = "PRINTS_MENU")
            @PathVariable PrintTemplateUsageContext usageContext) {
        return ResponseEntity.ok(service.getActiveByUsageContext(usageContext));
    }

    @GetMapping("/{printTemplateId}")
    @Operation(summary = "Return print template by ID", description = "Returns one print template with the full pdfme JSON payload.")
    @ApiResponse(responseCode = "200", description = "Print template found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrintTemplateDto.class)))
    @ApiResponse(responseCode = "404", description = "Print template not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<PrintTemplateDto> getPrintTemplateById(
            @Parameter(required = true, description = "Print template ID", example = "1")
            @PathVariable Long printTemplateId) {
        return ResponseEntity.ok(service.getById(printTemplateId));
    }

    @PostMapping
    @Operation(summary = "Create print template", description = "Creates a new print template storing the pdfme Template JSON.")
    @ApiResponse(responseCode = "201", description = "Print template created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrintTemplateDto.class)))
    public ResponseEntity<PrintTemplateDto> createPrintTemplate(@Valid @RequestBody PrintTemplateCreateDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input));
    }

    @PutMapping("/{printTemplateId}")
    @Operation(summary = "Update print template", description = "Updates an existing print template and its pdfme Template JSON.")
    @ApiResponse(responseCode = "200", description = "Print template updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrintTemplateDto.class)))
    @ApiResponse(responseCode = "404", description = "Print template not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<PrintTemplateDto> updatePrintTemplate(
            @Parameter(required = true, description = "Print template ID", example = "1")
            @PathVariable Long printTemplateId,
            @Valid @RequestBody PrintTemplateUpdateDto input) {
        return ResponseEntity.ok(service.update(printTemplateId, input));
    }

    @DeleteMapping("/{printTemplateId}")
    @Operation(summary = "Delete print template", description = "Deletes a print template by ID.")
    @ApiResponse(responseCode = "204", description = "Print template deleted")
    @ApiResponse(responseCode = "404", description = "Print template not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<Void> deletePrintTemplate(
            @Parameter(required = true, description = "Print template ID", example = "1")
            @PathVariable Long printTemplateId) {
        service.delete(printTemplateId);
        return ResponseEntity.noContent().build();
    }
}
