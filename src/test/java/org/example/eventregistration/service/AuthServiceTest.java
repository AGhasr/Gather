package org.example.eventregistration.service;

import org.example.eventregistration.dto.AuthRequest;
import org.example.eventregistration.dto.AuthResponse;
import org.example.eventregistration.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserService userService;
    private AuthenticationManager authManager;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        authManager = Mockito.mock(AuthenticationManager.class);
        jwtService = Mockito.mock(JwtService.class);

        // Ensure this order matches your AuthService constructor!
        authService = new AuthService(authManager, jwtService, userService);
    }

    @Test
    void register_shouldRegisterUserAndReturnTheUser() {
        // given
        String username = "username";
        String password = "password";
        String email = "test@example.com";

        // Setup AuthRequest with email
        AuthRequest authRequest = new AuthRequest(username, password, email);

        // Expected User result
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setEmail(email);

        when(userService.registerUser(username, password, email))
                .thenReturn(expectedUser);

        // when
        User result = authService.register(authRequest);

        // then
        assertThat(result).isEqualTo(expectedUser);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getEmail()).isEqualTo(email);

        verify(userService).registerUser(username, password, email);
    }

    @Test
    void login_shouldAuthenticateAndReturnToken() {
        // given
        String username = "username";
        String password = "password";
        String testToken = "testToken";

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(username);
        authRequest.setPassword(password);

        when(jwtService.generateToken(username)).thenReturn(testToken);

        // when
        AuthResponse authResponse = authService.login(authRequest);

        // then
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getToken()).isEqualTo(testToken);

        verify(jwtService).generateToken(username);
        // Verify authentication manager was called
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}