package com.samreen.medicationsafety.auth.security;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.samreen.medicationsafety.auth.entity.Role;
import com.samreen.medicationsafety.auth.entity.User;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String rawSecret =
                "0123456789012345678901234567890123456789012345678901234567890123";

        String base64Secret = Base64.getEncoder()
                .encodeToString(
                    rawSecret.getBytes(StandardCharsets.UTF_8)
                );

        jwtService = new JwtService(
                base64Secret,
                60_000
        );
    }

    @Test
    void generatedTokenShouldContainEmailAndRole() {
        User user = new User();
        user.setName("Samreen Test");
        user.setEmail("samreen@example.com");
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(
                "samreen@example.com",
                jwtService.extractEmail(token)
        );

        assertEquals(
                "USER",
                jwtService.extractRole(token)
        );

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void tamperedTokenShouldBeInvalid() {
        User user = new User();
        user.setName("Samreen Test");
        user.setEmail("samreen@example.com");
        user.setRole(Role.ADMIN);

        String token = jwtService.generateToken(user);

        String tamperedToken =
                token.substring(0, token.length() - 2) + "xx";

        assertFalse(
                jwtService.isTokenValid(tamperedToken)
        );
    }

    @Test
    void expiredTokenShouldBeInvalid() throws InterruptedException {
        String rawSecret =
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789AB";

        String base64Secret = Base64.getEncoder()
                .encodeToString(
                    rawSecret.getBytes(StandardCharsets.UTF_8)
                );

        JwtService shortLivedJwtService =
                new JwtService(base64Secret, 1);

        User user = new User();
        user.setName("Samreen Test");
        user.setEmail("samreen@example.com");
        user.setRole(Role.USER);

        String token =
                shortLivedJwtService.generateToken(user);

        Thread.sleep(10);

        assertFalse(
                shortLivedJwtService.isTokenValid(token)
        );
    }
}
