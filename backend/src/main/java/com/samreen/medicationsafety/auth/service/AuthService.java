package com.samreen.medicationsafety.auth.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samreen.medicationsafety.auth.dto.AuthResponse;
import com.samreen.medicationsafety.auth.dto.LoginRequest;
import com.samreen.medicationsafety.auth.dto.RegisterRequest;
import com.samreen.medicationsafety.auth.entity.Role;
import com.samreen.medicationsafety.auth.entity.User;
import com.samreen.medicationsafety.auth.repository.UserRepository;
import com.samreen.medicationsafety.auth.security.JwtService;
import com.samreen.medicationsafety.exception.DuplicateResourceException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String normalizedEmail =
                request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException(
                    "An account with this email already exists"
            );
        }

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return toAuthResponse(savedUser, token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        String normalizedEmail =
                request.getEmail().trim().toLowerCase();

        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user);

        return toAuthResponse(user, token);
    }

    private AuthResponse toAuthResponse(
            User user,
            String token) {

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                token
        );
    }
}
