package org.example.eventregistration.repository;

import org.example.eventregistration.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // NEW: Needed for registration duplicate check
    Optional<User> findByEmail(String email);

    // NEW: Needed for "Login with Username OR Email" logic
    Optional<User> findByUsernameOrEmail(String username, String email);
}