package org.example.eventregistration.config;

import org.example.eventregistration.model.Event;
import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.EventRepository;
import org.example.eventregistration.repository.GroupRepository;
import org.example.eventregistration.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader {

    private final EventRepository repo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepo;

    public DataLoader(EventRepository repo, UserRepository userRepo, PasswordEncoder passwordEncoder, GroupRepository groupRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.groupRepo = groupRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        // Prevent re-seeding data on application restart
        if (userRepo.count() > 0) {
            return;
        }

        // Initialize default users
        User admin = new User("admin", passwordEncoder.encode("admin"), "ADMIN", "admin@gather.app");
        User alice = new User("alice", passwordEncoder.encode("1234"), "USER", "alice@example.com");
        User bob = new User("bob", passwordEncoder.encode("1234"), "USER", "bob@example.com");

        userRepo.save(admin);
        userRepo.save(alice);
        userRepo.save(bob);

        // Initialize groups and assign members
        Group parisTrip = new Group("Paris 2025", admin);
        parisTrip.getMembers().add(alice);

        groupRepo.save(parisTrip);

        // Seed initial events
        repo.save(new Event("Eiffel Tower Visit", "Sightseeing", LocalDate.now().plusDays(5), parisTrip));
        repo.save(new Event("Dinner at Le Petit", "Food", LocalDate.now().plusDays(5), parisTrip));

        System.out.println("Application data seeding completed.");
    }
}