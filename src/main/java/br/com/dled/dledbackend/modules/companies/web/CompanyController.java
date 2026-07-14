package br.com.dled.dledbackend.modules.companies.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.companies.application.ICompanyService;
import br.com.dled.dledbackend.modules.companies.application.dto.CompanyDto;
import br.com.dled.dledbackend.modules.companies.application.dto.CompanyUpsertDto;
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
@RequestMapping("${api.prefix}/companies")
@Tag(name = "Company", description = "Company Controller")
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "authCookie")
public class CompanyController {
    private final ICompanyService service;

    @GetMapping
    @Operation(summary = "Return list with all companies", description = "Returns all companies registered in the system. The type field uses OWN for internal companies and SUPPLIER for vendors.")
    @ApiResponse(responseCode = "200", description = "Companies returned successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyDto.class)))
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "Return company by ID", description = "Returns one company by ID with short name, full name, type and creation date.")
    @ApiResponse(responseCode = "200", description = "Company found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyDto.class)))
    @ApiResponse(responseCode = "404", description = "Company not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<CompanyDto> getCompanyById(
            @Parameter(required = true, description = "Company ID", example = "1")
            @PathVariable Long companyId) {
        return ResponseEntity.ok(service.getById(companyId));
    }

    @PostMapping
    @Operation(summary = "Create company", description = "Creates a new company. Use OWN for the internal company and SUPPLIER for vendors or providers.")
    @ApiResponse(responseCode = "201", description = "Company created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyDto.class)))
    public ResponseEntity<CompanyDto> createCompany(@Valid @RequestBody CompanyUpsertDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input));
    }

    @PutMapping("/{companyId}")
    @Operation(summary = "Update company", description = "Updates an existing company with short name, full name and company type.")
    @ApiResponse(responseCode = "200", description = "Company updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyDto.class)))
    @ApiResponse(responseCode = "404", description = "Company not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<CompanyDto> updateCompany(
            @Parameter(required = true, description = "Company ID", example = "1")
            @PathVariable Long companyId,
            @Valid @RequestBody CompanyUpsertDto input) {
        return ResponseEntity.ok(service.update(companyId, input));
    }

    @DeleteMapping("/{companyId}")
    @Operation(summary = "Delete company", description = "Deletes a company by ID.")
    @ApiResponse(responseCode = "204", description = "Company deleted")
    @ApiResponse(responseCode = "404", description = "Company not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<Void> deleteCompany(
            @Parameter(required = true, description = "Company ID", example = "1")
            @PathVariable Long companyId) {
        service.delete(companyId);
        return ResponseEntity.noContent().build();
    }
}
