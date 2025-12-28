package org.example.eventregistration.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inject fake secret key for testing
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        jwtService.init();
    }

    @Test
    void generateToken_shouldBeValid() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        assertThat(token).isNotNull();
        assertThat(jwtService.isTokenValid(token, username)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo(username);
    }

    @Test
    void extractUsername_shouldThrowOnInvalidToken() {
        assertThrows(JwtException.class, () -> jwtService.extractUsername("invalid.token.here"));
    }
}