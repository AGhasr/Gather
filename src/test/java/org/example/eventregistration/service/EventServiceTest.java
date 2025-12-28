package org.example.eventregistration.service;

import org.example.eventregistration.model.Event;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.EventRepository;
import org.example.eventregistration.repository.UserRepository;
import org.example.eventregistration.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private GroupRepository groupRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, userRepository, groupRepository);
    }

    @Test
    void createEvent_shouldSaveEvent() {
        Event event = new Event();
        event.setTitle("Party");
        event.setDescription("Fun");
        event.setDate(LocalDate.now().plusDays(1));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event created = eventService.createEvent(event);
        assertThat(created).isNotNull();
        verify(eventRepository).save(event);
    }

    @Test
    void registerUserForEvent_shouldAddUser() {
        Long eventId = 1L;
        String username = "ali";

        User user = new User();
        user.setUsername(username);

        Event event = new Event();
        event.setId(eventId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.registerUserForEvent(username, eventId);

        verify(userRepository).save(user);
        assertThat(user.getRegisteredEvents()).contains(event);
    }

    @Test
    void deleteEvent_shouldRemoveFromUsers() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);

        User user = new User();
        user.getRegisteredEvents().add(event);
        event.getParticipants().add(user);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.deleteEvent(eventId);

        verify(userRepository).save(user); // Should save user to update relationship
        verify(eventRepository).delete(event);
        assertThat(user.getRegisteredEvents()).isEmpty();
    }
}