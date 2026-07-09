package br.com.dled.dledbackend.modules.printtemplates.application.dto;

import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplateUsageContext;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PrintTemplateCreateDto(
        @Schema(description = "Display name", example = "Etiqueta de mes")
        @NotBlank
        String name,
        @Schema(description = "Optional description", example = "Etiqueta mensal 25x33mm")
        String description,
        @Schema(description = "Where the template should be available", example = "PRINTS_MENU")
        @NotNull
        PrintTemplateUsageContext usageContext,
        @Schema(description = "Whether the template is available for common print flows", example = "true")
        Boolean active,
        @Schema(description = "pdfme Template JSON")
        @NotBlank
        String templateJson,
        @Schema(description = "Template width in millimeters", example = "25")
        @Positive
        Double widthMm,
        @Schema(description = "Template height in millimeters", example = "33")
        @Positive
        Double heightMm
) {
}
