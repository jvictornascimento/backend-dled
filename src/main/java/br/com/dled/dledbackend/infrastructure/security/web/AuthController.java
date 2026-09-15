package br.com.dled.dledbackend.infrastructure.security.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.infrastructure.security.JwtProperties;
import br.com.dled.dledbackend.infrastructure.security.JwtService;
import br.com.dled.dledbackend.infrastructure.security.dto.AuthResponse;
import br.com.dled.dledbackend.infrastructure.security.dto.LoginRequest;
import br.com.dled.dledbackend.infrastructure.security.dto.LogoutResponse;
import br.com.dled.dledbackend.modules.users.application.dto.UserDto;
import br.com.dled.dledbackend.modules.users.application.mapper.IUserMapper;
import br.com.dled.dledbackend.modules.users.domain.UserAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/auth")
@Tag(name = "Authentication", description = "Authentication Controller")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final LoginAttemptService loginAttemptService;
    private final IUserMapper userMapper;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Public endpoint that authenticates a user, returns a JWT and stores it in an HttpOnly cookie.")
    @ApiResponse(responseCode = "200", description = "Authenticated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest input, HttpServletRequest request, HttpServletResponse response) {
        String clientAddress = request.getRemoteAddr();
        loginAttemptService.ensureLoginAllowed(input.username(), clientAddress);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.username(), input.password())
            );
        } catch (BadCredentialsException ex) {
            loginAttemptService.loginFailed(input.username(), clientAddress);
            throw ex;
        }

        UserAccount user = (UserAccount) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        loginAttemptService.loginSucceeded(input.username(), clientAddress);
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(token, false).toString());

        return ResponseEntity.ok(new AuthResponse(token, "Bearer", userMapper.fromOut(user)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Clears the authentication cookie.")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    @ApiResponse(responseCode = "200", description = "Logged out successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LogoutResponse.class)))
    public ResponseEntity<LogoutResponse> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("", true).toString());
        return ResponseEntity.ok(new LogoutResponse("Logout successful"));
    }

    @GetMapping("/me")
    @Operation(summary = "Return authenticated user", description = "Returns the user associated with the current JWT cookie or bearer token.")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    @ApiResponse(responseCode = "200", description = "Authenticated user returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "401", description = "Missing or invalid authentication",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal UserAccount user) {
        return ResponseEntity.ok(userMapper.fromOut(user));
    }

    private ResponseCookie buildCookie(String value, boolean clear) {
        return ResponseCookie.from(jwtProperties.getCookieName(), value)
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite(jwtProperties.getCookieSameSite())
                .path("/")
                .maxAge(clear ? 0 : jwtProperties.getExpirationMinutes() * 60)
                .build();
    }
}
