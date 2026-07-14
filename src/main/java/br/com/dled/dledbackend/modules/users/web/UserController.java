package br.com.dled.dledbackend.modules.users.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.modules.users.application.IUserService;
import br.com.dled.dledbackend.modules.users.application.dto.UserCreateDto;
import br.com.dled.dledbackend.modules.users.application.dto.UserDto;
import br.com.dled.dledbackend.modules.users.application.dto.UserUpdateDto;
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
@RequestMapping("${api.prefix}/users")
@Tag(name = "User", description = "User Controller")
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "authCookie")
public class UserController {
    private final IUserService service;

    @GetMapping
    @Operation(summary = "Return all users", description = "Returns all registered users without exposing passwords.")
    @ApiResponse(responseCode = "200", description = "Users returned successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Return user by ID")
    @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<UserDto> getUserById(
            @Parameter(required = true, description = "User ID", example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.getById(userId));
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Creates a new application user with conventional profile data and an unrelated plain-text companyName field.")
    @ApiResponse(responseCode = "201", description = "User created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    @ApiResponse(responseCode = "409", description = "Username or email already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    @ApiResponse(responseCode = "409", description = "Username or email already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<UserDto> updateUser(
            @Parameter(required = true, description = "User ID", example = "1")
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDto input) {
        return ResponseEntity.ok(service.update(userId, input));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<Void> deleteUser(
            @Parameter(required = true, description = "User ID", example = "1")
            @PathVariable Long userId) {
        service.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
