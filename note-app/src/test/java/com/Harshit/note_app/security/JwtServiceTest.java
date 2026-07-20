package com.Harshit.note_app.security;

import com.Harshit.note_app.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "this-is-a-very-long-secret-key-for-jwt-signing-32chars-min";

    private JwtService jwtService;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L);

        User user = new User();
        user.setEmail("jane@example.com");
        user.setUsername("jane");
        userDetails = new CustomUserDetails(user);
    }

    @Test
    void generateToken_createsNonEmptySignedToken() {
        // Verifies token generation produces a usable JWT string for a valid user.
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("jane@example.com", jwtService.extractUsername(token));
    }

    @Test
    void extractUsername_returnsEmailFromTokenSubject() {
        // Verifies the JWT subject stores and returns the user's email (login identifier).
        String token = jwtService.generateToken(userDetails);

        assertEquals("jane@example.com", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_returnsTrueForMatchingUserAndValidToken() {
        // Verifies a freshly issued token is valid for the same user details.
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_returnsFalseWhenUsernameDoesNotMatch() {
        // Verifies tokens cannot be reused for a different user's credentials.
        String token = jwtService.generateToken(userDetails);

        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        CustomUserDetails otherUserDetails = new CustomUserDetails(otherUser);

        assertFalse(jwtService.isTokenValid(token, otherUserDetails));
    }

    @Test
    void isTokenValid_rejectsExpiredToken() {
        JwtService expiredJwtService = new JwtService(SECRET, -1_000L);
        String token = expiredJwtService.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> expiredJwtService.isTokenValid(token, userDetails));
    }

    @Test
    void generateToken_worksWithShortSecret() {
        // Railway secrets are sometimes short; signing must still produce a valid HMAC key.
        JwtService shortSecretService = new JwtService("short", 3_600_000L);
        String token = shortSecretService.generateToken(userDetails);

        assertEquals("jane@example.com", shortSecretService.extractUsername(token));
        assertTrue(shortSecretService.isTokenValid(token, userDetails));
    }
}
