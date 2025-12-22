package org.example.eventregistration.repository;

import org.example.eventregistration.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e JOIN e.group g JOIN g.members m WHERE m.username = :username AND e.archived = false")
    List<Event> findActiveEventsForUser(@Param("username") String username);

    @Query("SELECT e FROM Event e JOIN e.group g JOIN g.members m WHERE m.username = :username AND e.archived = true")
    List<Event> findArchivedEventsForUser(@Param("username") String username);
}