package br.com.dled.dledbackend.core.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record StandardError(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "GTM")
        @Schema(description = "Timestamp of the error", example = "2026-04-10T14:30:00Z")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "500")
        Integer status,
        @Schema(description = "HTTP reason phrase", example = "Internal Server Error")
        String error,
        @Schema(description = "Detailed error message", example = "An error occurred during processing")
        String message,
        @Schema(description = "Request path", example = "/v1/products/1")
        String path){
}
