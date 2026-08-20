package com.vikas.cowselling.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails user;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService(
                "this-is-a-very-long-secret-key-for-testing-only-123456789"
        );

        user = org.springframework.security.core.userdetails.User
                .withUsername("seller@example.com")
                .password("password")
                .roles("SELLER")
                .build();
    }

    @Test
    void shouldGenerateAndValidateToken() {

        String token = jwtService.generateToken(user);

        assertNotNull(token);

        String username = jwtService.extractUsername(token);

        assertEquals(
                "seller@example.com",
                username
        );

        assertTrue(
                jwtService.isTokenValid(
                        token,
                        user
                )
        );
    }
}