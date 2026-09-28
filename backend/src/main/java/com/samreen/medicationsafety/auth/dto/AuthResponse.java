package com.samreen.medicationsafety.auth.dto;

public class AuthResponse {

    private final Long userId;
    private final String name;
    private final String email;
    private final String role;
    private final String token;

    public AuthResponse(
            Long userId,
            String name,
            String email,
            String role,
            String token) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}
