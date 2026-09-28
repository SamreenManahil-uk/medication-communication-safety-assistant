package com.samreen.medicationsafety.config;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.samreen.medicationsafety.auth.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .exceptionHandling(exceptions -> exceptions

                .authenticationEntryPoint(
                    (request, response, exception) ->
                        writeError(
                            response,
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Unauthorized",
                            "Authentication is required"
                        )
                )

                .accessDeniedHandler(
                    (request, response, exception) ->
                        writeError(
                            response,
                            HttpServletResponse.SC_FORBIDDEN,
                            "Forbidden",
                            "You do not have permission to access this resource"
                        )
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/health",
                    "/actuator/health",
                    "/error"
                ).permitAll()

                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/analyses/**"
                ).hasAnyRole("USER", "ADMIN")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/analyses/**"
                ).hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                    "http://127.0.0.1:5173",
                    "http://127.0.0.1:5174",
                    "http://localhost:5173",
                    "http://localhost:5174"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String error,
            String message) throws IOException {

        response.setStatus(status);

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        String json = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                Instant.now(),
                status,
                error,
                message
        );

        response.getWriter().write(json);
    }
}
