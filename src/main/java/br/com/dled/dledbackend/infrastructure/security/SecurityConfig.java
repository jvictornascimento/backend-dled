package br.com.dled.dledbackend.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {
    static final String CONTENT_SECURITY_POLICY = """
            default-src 'self'; \
            base-uri 'self'; \
            object-src 'none'; \
            frame-ancestors 'none'; \
            form-action 'self'; \
            img-src 'self' data: https:; \
            script-src 'self' 'unsafe-inline'; \
            style-src 'self' 'unsafe-inline'; \
            connect-src 'self'; \
            font-src 'self' data:; \
            upgrade-insecure-requests\
            """;

    static final String PERMISSIONS_POLICY = """
            accelerometer=(), \
            autoplay=(), \
            camera=(), \
            display-capture=(), \
            encrypted-media=(), \
            fullscreen=(self), \
            geolocation=(), \
            gyroscope=(), \
            magnetometer=(), \
            microphone=(), \
            midi=(), \
            payment=(), \
            picture-in-picture=(), \
            publickey-credentials-get=(), \
            sync-xhr=(), \
            usb=(), \
            xr-spatial-tracking=()\
            """;

    @Value("${api.prefix}")
    private String apiPrefix;

    private final ApplicationUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(apiPrefix + "/auth/login"))
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31_536_000))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(CONTENT_SECURITY_POLICY))
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .permissionsPolicyHeader(policy -> policy.policy(PERMISSIONS_POLICY)))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(apiPrefix + "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                apiPrefix + "/products",
                                apiPrefix + "/products/*",
                                apiPrefix + "/categories/root",
                                apiPrefix + "/categories/tree",
                                apiPrefix + "/categories/*",
                                apiPrefix + "/wood/products",
                                apiPrefix + "/wood/products/*",
                                apiPrefix + "/wood/categories",
                                apiPrefix + "/wood/categories/root",
                                apiPrefix + "/wood/categories/*"
                        ).permitAll()
                        .requestMatchers(apiPrefix + "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                apiPrefix + "/products/**",
                                apiPrefix + "/wood/products/**",
                                apiPrefix + "/orders/**",
                                apiPrefix + "/print-templates/**"
                        ).hasAnyRole("ADMIN", "EMPLOY")
                        .requestMatchers(HttpMethod.PUT,
                                apiPrefix + "/products/**",
                                apiPrefix + "/wood/products/**",
                                apiPrefix + "/orders/**",
                                apiPrefix + "/print-templates/**"
                        ).hasAnyRole("ADMIN", "EMPLOY")
                        .requestMatchers(HttpMethod.DELETE,
                                apiPrefix + "/products/**",
                                apiPrefix + "/wood/products/**",
                                apiPrefix + "/orders/**",
                                apiPrefix + "/print-templates/**"
                        ).hasAnyRole("ADMIN", "EMPLOY")
                        .requestMatchers(HttpMethod.POST,
                                apiPrefix + "/categories/**",
                                apiPrefix + "/wood/categories/**",
                                apiPrefix + "/companies/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                apiPrefix + "/categories/**",
                                apiPrefix + "/wood/categories/**",
                                apiPrefix + "/companies/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                apiPrefix + "/categories/**",
                                apiPrefix + "/wood/categories/**",
                                apiPrefix + "/companies/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, apiPrefix + "/companies/**").hasAnyRole("ADMIN", "USER", "EMPLOY")
                        .requestMatchers(HttpMethod.GET, apiPrefix + "/orders/**").hasAnyRole("ADMIN", "USER", "EMPLOY", "SELLER")
                        .requestMatchers(HttpMethod.GET, apiPrefix + "/print-templates/**").hasAnyRole("ADMIN", "USER", "EMPLOY", "SELLER")
                        .requestMatchers(apiPrefix + "/**").hasAnyRole("ADMIN", "USER", "EMPLOY", "SELLER")
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
