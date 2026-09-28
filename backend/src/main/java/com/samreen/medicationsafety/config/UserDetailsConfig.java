package com.samreen.medicationsafety.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.samreen.medicationsafety.auth.entity.User;
import com.samreen.medicationsafety.auth.repository.UserRepository;

@Configuration
public class UserDetailsConfig {

    @Bean
    public UserDetailsService userDetailsService(
            UserRepository userRepository) {

        return username -> {

            String normalizedEmail =
                    username.trim().toLowerCase();

            User user = userRepository
                    .findByEmail(normalizedEmail)
                    .orElseThrow(() ->
                        new UsernameNotFoundException(
                            "User not found"
                        )
                    );

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();
        };
    }
}
