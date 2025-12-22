//package org.example.eventregistration.service;
//
//import org.example.eventregistration.model.User;
//import org.example.eventregistration.repository.EventRepository;
//import org.example.eventregistration.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mockito;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UserServiceTest {
//
//    private UserRepository userRepository;
//    private UserService userService;
//    private PasswordEncoder passwordEncoder;
//
//
//    @BeforeEach
//    void setUp() {
//        userRepository = Mockito.mock(UserRepository.class);
//        passwordEncoder = Mockito.mock(PasswordEncoder.class);
//        userService = new UserService(userRepository, passwordEncoder);
//    }
//
//    @Test
//    void findByUsername_shouldReturnUser() {
//
//        //given
//        String username = "testUser";
//        User testUser = new User();
//        testUser.setUsername(username);
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
//
//        //when
//        Optional<User> user = userService.findByUsername(username);
//
//        //then
//        assertThat( user.isPresent()).isTrue();
//        assertThat(user.get().getUsername()).isEqualTo(username);
//
//        verify(userRepository).findByUsername(username);
//    }
//
//    @Test
//    void registerUser_shouldRegisterUserAndReturnUser() {
//
//        //given
//        String username = "testUser";
//        String rawPassword = "testPassword";
//        String encodedPassword = "encodedSecret";
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
//
//        //when
//        User result = userService.registerUser(username, rawPassword,"USER");
//
//        //then
//        verify(userRepository).findByUsername(username);
//        verify(passwordEncoder).encode(rawPassword);
//
//        // capture the user that was saved
//        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
//        verify(userRepository).save(captor.capture());
//        User savedUser = captor.getValue();
//
//        assertThat(savedUser).isNotNull();
//        assertThat(savedUser.getUsername()).isEqualTo(username);
//        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
//        assertThat(savedUser.getRole()).isEqualTo("USER");
//
//        assertThat(result).isEqualTo(savedUser);
//
//    }
//
//    @Test
//    void registerUser_shouldThrowExceptionWhenUsernameAlreadyExists() {
//
//        //given
//        String username = "testUser";
//        String rawPassword = "testPassword";
//
//        User testUser = new User();
//        testUser.setUsername(username);
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
//
//        //when + then
//        assertThrows(IllegalArgumentException.class,
//                () -> userService.registerUser(username, rawPassword,"USER"));
//
//        verify(userRepository).findByUsername(username);
//
//        verify(passwordEncoder, never()).encode(rawPassword);
//        verify(userRepository, never()).save(testUser);
//
//    }
//}