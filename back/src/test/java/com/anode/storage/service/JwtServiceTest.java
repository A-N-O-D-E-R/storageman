package com.anode.storage.service;

import com.anode.storage.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JWT Service Tests")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private String testUsername;

    @BeforeEach
    void setUp() {
        testUsername = "testuser@example.com";
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken() {
        String token = jwtService.generateToken(testUsername);

        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        assertTrue(token.split("\\.").length == 3, "Token should have 3 parts separated by dots");
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void testExtractUsername() {
        String token = jwtService.generateToken(testUsername);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(testUsername, extractedUsername, "Extracted username should match original");
    }

    @Test
    @DisplayName("Should validate token for correct username")
    void testValidateToken() {
        String token = jwtService.generateToken(testUsername);

        boolean isValid = jwtService.validateToken(token, testUsername);

        assertTrue(isValid, "Token should be valid for the correct username");
    }

    @Test
    @DisplayName("Should reject token for incorrect username")
    void testValidateTokenWithWrongUsername() {
        String token = jwtService.generateToken(testUsername);

        boolean isValid = jwtService.validateToken(token, "wronguser@example.com");

        assertFalse(isValid, "Token should be invalid for incorrect username");
    }

    @Test
    @DisplayName("Should handle invalid token format")
    void testInvalidTokenFormat() {
        String invalidToken = "invalid.token.format";

        assertThrows(Exception.class, () -> {
            jwtService.extractUsername(invalidToken);
        }, "Should throw exception for invalid token");
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void testUniquenessOfTokens() {
        String token1 = jwtService.generateToken("user1@example.com");
        String token2 = jwtService.generateToken("user2@example.com");

        assertNotEquals(token1, token2, "Tokens for different users should be different");
    }
}
