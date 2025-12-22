//package org.example.eventregistration.service;
//
//import io.jsonwebtoken.JwtException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//class JwtServiceTest {
//
//    private JwtService jwtService;
//
//    @BeforeEach
//    void setUp() {
//        jwtService = new JwtService();
//        // inject a secret key manually
//        ReflectionTestUtils.setField(jwtService, "secretKey", "test-secret-key-123456789012345678901234");
//        jwtService.init();
//    }
//
//    @Test
//    void generateToken_shouldContainUsername() {
//        // given
//        String username = "testUser";
//
//        // when
//        String token = jwtService.generateToken(username);
//
//        // then
//        assertThat(token).isNotNull();
//        String extracted = jwtService.extractUsername(token);
//        assertThat(extracted).isEqualTo(username);
//    }
//
//    @Test
//    void extractUsername_shouldReturnCorrectUsername() {
//        // given
//        String username = "test";
//        String token = jwtService.generateToken(username);
//
//        // when
//        String extracted = jwtService.extractUsername(token);
//
//        // then
//        assertThat(extracted).isEqualTo(username);
//    }
//
//    @Test
//    void isTokenValid_shouldReturnTrueForCorrectUser() {
//        // given
//        String username = "test";
//        String token = jwtService.generateToken(username);
//
//        // when
//        boolean valid = jwtService.isTokenValid(token, username);
//
//        // then
//        assertThat(valid).isTrue();
//    }
//
//    @Test
//    void isTokenValid_shouldReturnFalseForWrongUser() {
//        // given
//        String token = jwtService.generateToken("test");
//
//        // when
//        boolean valid = jwtService.isTokenValid(token, "Ali");
//
//        // then
//        assertThat(valid).isFalse();
//    }
//
//    @Test
//    void extractUsername_shouldThrowForInvalidToken() {
//        // given
//        String invalidToken = "this.is.not.a.valid.token";
//
//        // when + then
//        assertThrows(JwtException.class, () -> jwtService.extractUsername(invalidToken));
//    }
//
//}
