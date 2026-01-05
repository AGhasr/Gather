package org.example.eventregistration.config;

import org.example.eventregistration.model.Event;
import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.EventRepository;
import org.example.eventregistration.repository.GroupRepository;
import org.example.eventregistration.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@ConditionalOnProperty(name = "app.db.seed", havingValue = "true", matchIfMissing = true)
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
        if (userRepo.count() > 0) return;
        System.out.println("✅ DEMO MODE: Database seeded with test users (ali/tom).");
        System.out.println("   (To disable this in production, set app.db.seed=false)");

        // Initialize default users
        User ali = new User("ali", passwordEncoder.encode("1234"), "ali@example.com");
        User tom = new User("tom", passwordEncoder.encode("1234"), "tom@example.com");
        ali.setEnabled(true);
        tom.setEnabled(true);


        userRepo.save(ali);
        userRepo.save(tom);

        // Initialize groups and assign members
        Group londonTrip = new Group("Arsenal Matchday 2026", ali);
        londonTrip.getMembers().add(tom);

        groupRepo.save(londonTrip);

        // Seed initial events for the Arsenal trip from Düsseldorf
        repo.save(new Event("Flight DUS -> LHR", "Eurowings EW9460. Meet at DUS Terminal A, Gate A40 at 06:30.", LocalDate.now().plusDays(2), londonTrip));
        repo.save(new Event("Stansted Express / Transfer", "Train into London Liverpool Street. Have Oyster cards ready.", LocalDate.now().plusDays(2), londonTrip));
        repo.save(new Event("Check-in at Highbury Hotel", "2 mins walk from the station. Reservation under 'Gunners Group'.", LocalDate.now().plusDays(2), londonTrip));
        repo.save(new Event("Pre-match Pints @ The Tollington", "Meeting the local fan club here before kickoff.", LocalDate.now().plusDays(3), londonTrip));
        repo.save(new Event("Arsenal vs Tottenham", "North London Derby! Kickoff 15:00 at Emirates Stadium. WEAR RED.", LocalDate.now().plusDays(3), londonTrip));
        repo.save(new Event("Emirates Stadium Tour", "Guided Legends Tour. Meet at the Armoury Store.", LocalDate.now().plusDays(4), londonTrip));

        System.out.println("Application data seeding completed.");
    }
}