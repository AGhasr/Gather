package org.example.eventregistration.service;

import org.example.eventregistration.model.Event;
import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.EventRepository;
import org.example.eventregistration.repository.GroupRepository;
import org.example.eventregistration.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findAll().stream()
                .filter(event -> !event.getDate().isBefore(LocalDate.now()))
                .toList();
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }


    @Transactional
    public void deleteEvent(Long eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        if (!event.getGroup().getAdmin().getUsername().equals(username)) {
            throw new IllegalStateException("Only the Group Creator can delete this event.");
        }

        for (User user : event.getParticipants()) {
            user.getRegisteredEvents().remove(event);
            userRepository.save(user);
        }

        eventRepository.delete(event);
    }

    @Transactional
    public void registerUserForEvent(String username, Long eventId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        if (user.getRegisteredEvents().contains(event)) {
            throw new IllegalStateException("User already registered!");
        }
        user.getRegisteredEvents().add(event);
        userRepository.save(user);
    }

    @Transactional
    public void unregisterUserFromEvent(String username, Long eventId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!user.getRegisteredEvents().contains(event)) {
            throw new IllegalStateException("User not registered for this event!");
        }
        user.getRegisteredEvents().remove(event);
        userRepository.save(user);
    }

    public List<Event> getUserEvents(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if (user.getRegisteredEvents() == null) {
            return new ArrayList<>();
        }
        return user.getRegisteredEvents();
    }

    public List<Event> getActiveEventsForUser(String username) {
        return eventRepository.findActiveEventsForUser(username);
    }

    public List<Event> getArchivedEventsForUser(String username) {
        return eventRepository.findArchivedEventsForUser(username);
    }

    @Transactional
    public void archiveEvent(Long eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!event.getGroup().getAdmin().getUsername().equals(username)) {
            throw new IllegalStateException("Only the Group Admin can archive events.");
        }

        event.setArchived(true);
        eventRepository.save(event);
    }

    @Transactional
    public Event createEventWithGroup(Event event, Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Group ID"));

        boolean isMember = group.getMembers().stream()
                .anyMatch(member -> member.getUsername().equals(username));

        if (!isMember) {
            throw new IllegalStateException("You can only create events for groups you belong to.");
        }

        event.setGroup(group);
        return eventRepository.save(event);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));
    }
}