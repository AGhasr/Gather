//package org.example.eventregistration.service;
//
//import org.example.eventregistration.dto.AuthRequest;
//import org.example.eventregistration.dto.AuthResponse;
//import org.example.eventregistration.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class AuthServiceTest {
//
//    private UserService userService;
//    private AuthenticationManager authManager;
//    private JwtService jwtService;
//    private AuthService authService;
//
//    @BeforeEach
//    void setUp() {
//        userService = Mockito.mock(UserService.class);
//        authManager = Mockito.mock(AuthenticationManager.class);
//        jwtService = Mockito.mock(JwtService.class);
//        authService = new AuthService(userService, authManager, jwtService);
//    }
//
//    @Test
//    void register_shouldRegisterUserAndReturnTheUser() {
//        //given
//        String username = "username";
//        String password = "password";
//        AuthRequest authRequest = new AuthRequest();
//        authRequest.setUsername(username);
//        authRequest.setPassword(password);
//
//        User expectedUser = new User(username, password, "USER");
//
//
//        when(userService.registerUser(username, password, "USER"))
//                .thenReturn(expectedUser);
//
//        //when
//        User result = authService.register(authRequest);
//
//        //then
//        assertThat( result ).isEqualTo( expectedUser );
//        assertThat( result.getUsername() ).isEqualTo( username );
//        assertThat( result.getPassword() ).isEqualTo( password );
//
//        verify(userService).registerUser(username, password, "USER");
//
//    }
//
//    @Test
//    void login_shouldAuthenticateAndReturnToken() {
//        //given
//        String username = "username";
//        String password = "password";
//        String testToken = "testToken";
//
//        AuthRequest authRequest = new AuthRequest();
//        authRequest.setUsername(username);
//        authRequest.setPassword(password);
//
//        when(jwtService.generateToken(username)).thenReturn(testToken);
//
//        //when
//        AuthResponse authResponse = authService.login(authRequest);
//
//        //then
//        assertThat(authResponse).isNotNull();
//        assertThat(authResponse.getToken()).isEqualTo(testToken);
//
//        verify(jwtService).generateToken(username);
//        verify(authManager).authenticate(new UsernamePasswordAuthenticationToken(username, password));
//
//    }
//}