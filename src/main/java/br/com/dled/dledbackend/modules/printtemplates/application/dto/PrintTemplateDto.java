package br.com.dled.dledbackend.modules.printtemplates.application.dto;

import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplateUsageContext;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PrintTemplateDto(
        @Schema(description = "Print template ID", example = "1")
        Long id,
        @Schema(description = "Display name", example = "Etiqueta de mes")
        String name,
        @Schema(description = "Optional description", example = "Etiqueta mensal 25x33mm")
        String description,
        @Schema(description = "Where the template should be available", example = "PRINTS_MENU")
        PrintTemplateUsageContext usageContext,
        @Schema(description = "Whether the template is available for common print flows", example = "true")
        Boolean active,
        @Schema(description = "pdfme Template JSON")
        String templateJson,
        @Schema(description = "Template width in millimeters", example = "25")
        Double widthMm,
        @Schema(description = "Template height in millimeters", example = "33")
        Double heightMm,
        @Schema(description = "Creation date and time")
        LocalDateTime createdAt,
        @Schema(description = "Last update date and time")
        LocalDateTime updatedAt
) {
}
