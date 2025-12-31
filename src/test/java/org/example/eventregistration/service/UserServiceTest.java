package org.example.eventregistration.service;

import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, emailService);
    }

    @Test
    void registerUser_shouldSaveUserWithEncodedPassword() {
        // given
        String username = "ali";
        String rawPass = "password";
        String email = "ali@test.com";
        String encodedPass = "encoded123";

        when(userRepository.findByUsernameOrEmail(username, email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPass)).thenReturn(encodedPass);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // when
        User result = userService.registerUser(username, rawPass, email, "USER");

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo(username);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPassword()).isEqualTo(encodedPass);
    }

    @Test
    void registerUser_shouldThrowIfUserExists() {
        // given
        when(userRepository.findByUsernameOrEmail("ali", "ali@test.com"))
                .thenReturn(Optional.of(new User()));

        // when + then
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("ali", "pass", "ali@test.com", "USER")
        );

        verify(userRepository, never()).save(any());
    }
}