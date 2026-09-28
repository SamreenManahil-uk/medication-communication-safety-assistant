package com.samreen.medicationsafety.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.samreen.medicationsafety.auth.dto.AuthResponse;
import com.samreen.medicationsafety.auth.dto.LoginRequest;
import com.samreen.medicationsafety.auth.dto.RegisterRequest;
import com.samreen.medicationsafety.auth.entity.Role;
import com.samreen.medicationsafety.auth.entity.User;
import com.samreen.medicationsafety.auth.repository.UserRepository;
import com.samreen.medicationsafety.auth.security.JwtService;
import com.samreen.medicationsafety.exception.DuplicateResourceException;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void registerShouldCreateUserWithUserRoleAndHashedPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Samreen Test");
        request.setEmail("SAMREEN@example.com");
        request.setPassword("SecurePassword123");

        when(userRepository.existsByEmail("samreen@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("SecurePassword123"))
                .thenReturn("HASHED_PASSWORD");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("test-jwt");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("Samreen Test", savedUser.getName());
        assertEquals("samreen@example.com", savedUser.getEmail());
        assertEquals("HASHED_PASSWORD", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());

        assertEquals("samreen@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
        assertEquals("test-jwt", response.getToken());
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Samreen");
        request.setEmail("samreen@example.com");
        request.setPassword("SecurePassword123");

        when(userRepository.existsByEmail("samreen@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldReturnJwtForCorrectCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("SAMREEN@example.com");
        request.setPassword("SecurePassword123");

        User user = createUser();

        when(userRepository.findByEmail("samreen@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "SecurePassword123",
                "HASHED_PASSWORD"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("login-jwt");

        AuthResponse response = authService.login(request);

        assertEquals("samreen@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
        assertEquals("login-jwt", response.getToken());
    }

    @Test
    void loginShouldRejectWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("samreen@example.com");
        request.setPassword("WrongPassword123");

        User user = createUser();

        when(userRepository.findByEmail("samreen@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword123",
                "HASHED_PASSWORD"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(any(User.class));
    }

    @Test
    void loginShouldRejectUnknownEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@example.com");
        request.setPassword("SecurePassword123");

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );
    }

    private User createUser() {
        User user = new User();
        user.setName("Samreen Test");
        user.setEmail("samreen@example.com");
        user.setPassword("HASHED_PASSWORD");
        user.setRole(Role.USER);
        return user;
    }
}
