package br.com.dled.dledbackend.infrastructure.security.web;

import br.com.dled.dledbackend.core.exceptions.StandardError;
import br.com.dled.dledbackend.infrastructure.security.JwtProperties;
import br.com.dled.dledbackend.infrastructure.security.JwtService;
import br.com.dled.dledbackend.infrastructure.security.dto.AuthResponse;
import br.com.dled.dledbackend.infrastructure.security.dto.LoginRequest;
import br.com.dled.dledbackend.infrastructure.security.dto.LogoutResponse;
import br.com.dled.dledbackend.modules.users.application.mapper.IUserMapper;
import br.com.dled.dledbackend.modules.users.domain.UserAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final IUserMapper userMapper;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Public endpoint that authenticates a user, returns a JWT and stores it in an HttpOnly cookie.")
    @ApiResponse(responseCode = "200", description = "Authenticated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class)))
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest input, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.username(), input.password())
        );

        UserAccount user = (UserAccount) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        response.addCookie(buildCookie(token, false));

        return ResponseEntity.ok(new AuthResponse(token, "Bearer", userMapper.fromOut(user)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Clears the authentication cookie.")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "authCookie")
    @ApiResponse(responseCode = "200", description = "Logged out successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LogoutResponse.class)))
    public ResponseEntity<LogoutResponse> logout(HttpServletResponse response) {
        response.addCookie(buildCookie("", true));
        return ResponseEntity.ok(new LogoutResponse("Logout successful"));
    }

    private Cookie buildCookie(String value, boolean clear) {
        Cookie cookie = new Cookie(jwtProperties.getCookieName(), value);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.isCookieSecure());
        cookie.setPath("/");
        cookie.setMaxAge(clear ? 0 : (int) (jwtProperties.getExpirationMinutes() * 60));
        return cookie;
    }
}
