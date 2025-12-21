package org.example.eventregistration.security;

import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        // "input" is whatever the user typed in the login box
        // It could be "bob" OR "bob@example.com"

        // We use the new repository method to check BOTH columns
        User user = userRepository.findByUsernameOrEmail(input, input)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + input));

        // Convert our Database User to a Spring Security User
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername()) // IMPORTANT: Use the real username (e.g. "bob") internally
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}