package br.com.dled.dledbackend.core.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record StandardError(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "GTM")
        @Schema(description = "Timestamp of the error")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "404")
        Integer status,
        String error,
        @Schema(description = "Error message", example = "Entity not found")
        String message,
        @Schema(description = "Request path", example = "/v1/entity/1")
        String path){
}
