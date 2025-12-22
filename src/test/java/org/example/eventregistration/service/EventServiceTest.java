//package org.example.eventregistration.service;
//
//import org.example.eventregistration.model.Event;
//import org.example.eventregistration.model.User;
//import org.example.eventregistration.repository.EventRepository;
//import org.example.eventregistration.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class EventServiceTest {
//
//    private EventRepository eventRepository;
//    private UserRepository userRepository;
//    private EventService eventService;
//
//    @BeforeEach
//    void setUp() {
//        eventRepository = Mockito.mock(EventRepository.class);
//        userRepository = Mockito.mock(UserRepository.class);
//        eventService = new EventService(eventRepository, userRepository);
//    }
//
//    @Test
//    void getUpcomingEvents_shouldReturnOnlyFutureEvents() {
//        //given
//        Event pastEvent = new Event("testPast", "test Event", LocalDate.now().minusDays(5));
//        Event futureEvent = new Event("testFuture", "test Event", LocalDate.now().plusDays(5));
//
//        when(eventRepository.findAll()).thenReturn(List.of(pastEvent, futureEvent));
//
//
//        //when
//        List<Event> upcomingEvents = eventService.getUpcomingEvents();
//
//        //then
//        assertEquals(1, upcomingEvents.size());
//        assertEquals(futureEvent, upcomingEvents.get(0));
//
//        verify(eventRepository).findAll();
//
//    }
//
//    @Test
//    void createEvent_shouldSaveAndReturnEvent() {
//
//        //given
//        Event testEvent = new Event("testEvent", "test Event", LocalDate.now().plusDays(5));
//        when(eventRepository.save(testEvent)).thenReturn(testEvent);
//
//        //when
//        Event createdEvent = eventService.createEvent(testEvent);
//
//        //then
//        assertThat(createdEvent).isNotNull().isEqualTo(testEvent);
//
//        verify(eventRepository).save(testEvent);
//    }
//
//    @Test
//    void deleteEvent_shouldDeleteEventAndUpdateUsers() {
//        // given
//        Long eventId = 1L;
//
//        Event event = new Event("Test Event", "Test Description", LocalDate.now().plusDays(1));
//        event.setId(eventId);
//
//        // Create test users
//        User user1 = new User();
//        user1.setId(1L);
//        user1.setUsername("user1");
//        user1.getRegisteredEvents().add(event);
//
//        User user2 = new User();
//        user2.setId(2L);
//        user2.setUsername("user2");
//        user2.getRegisteredEvents().add(event);
//
//        // Set up event participants
//        event.setParticipants(List.of(user1, user2));
//
//        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
//        when(userRepository.save(user1)).thenReturn(user1);
//        when(userRepository.save(user2)).thenReturn(user2);
//
//        // when
//        eventService.deleteEvent(eventId);
//
//        // Then
//        // Verify that the event was found
//        verify(eventRepository).findById(eventId);
//
//        // Verify that each user was saved after removing the event
//        verify(userRepository).save(user1);
//        verify(userRepository).save(user2);
//
//        // Verify that the event was deleted
//        verify(eventRepository).delete(event);
//
//        // Assert that users no longer have the event in their registered events
//        assertFalse(user1.getRegisteredEvents().contains(event));
//        assertFalse(user2.getRegisteredEvents().contains(event));
//    }
//
//    @Test
//    void deleteEvent_shouldThrowException_whenEventNotFound() {
//        // Given
//        Long eventId = 999L;
//        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> eventService.deleteEvent(eventId))
//                .isInstanceOf(NoSuchElementException.class);
//
//        // Verify that delete was never called
//        verify(eventRepository, never()).delete(any());
//        verify(userRepository, never()).save(any());
//    }
//
//    @Test
//    void registerUserForEvent_shouldSaveAndReturnUser() {
//        // given
//        Long eventId = 1L;
//
//        Event event = new Event("Test Event", "Test Description", LocalDate.now().plusDays(1));
//        event.setId(eventId);
//
//        // Create test users
//        String username = "user1";
//        User user1 = new User();
//        user1.setId(1L);
//        user1.setUsername(username);
//
//        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user1));
//
//        // when
//        eventService.registerUserForEvent(username, eventId);
//
//        // Then
//        verify(eventRepository).findById(eventId);
//
//        verify(userRepository).findByUsername(username);
//
//        verify(userRepository).save(user1);
//
//        assertTrue(user1.getRegisteredEvents().contains(event));
//    }
//
//    @Test
//    void registerUserForEvent_shouldThrowException_whenAlreadyRegisteredUser() {
//        // given
//        Long eventId = 1L;
//
//        Event event = new Event("Test Event", "Test Description", LocalDate.now().plusDays(1));
//        event.setId(eventId);
//
//        // Create test users
//        String username = "user1";
//        User user1 = new User();
//        user1.setId(1L);
//        user1.setUsername(username);
//        user1.getRegisteredEvents().add(event);
//
//
//        // Set up event participants
//        event.setParticipants(List.of(user1));
//
//        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user1));
//
//        // when
//        assertThatThrownBy(() -> eventService.registerUserForEvent(username, eventId))
//                .isInstanceOf(IllegalStateException.class)   // or your custom exception
//                .hasMessageContaining("User already registered!");
//
//        // Then
//        verify(eventRepository, never()).save(event);
//
//        assertTrue(user1.getRegisteredEvents().contains(event));
//    }
//
//    @Test
//    void unregisterUserFromEvent_shouldRemoveEventAndSaveUser() {
//        // given
//        Long eventId = 1L;
//        String username = "user1";
//
//        Event event = new Event("Test Event", "Test Description", LocalDate.now().plusDays(1));
//        event.setId(eventId);
//
//        User user = new User();
//        user.setId(1L);
//        user.setUsername(username);
//        user.getRegisteredEvents().add(event);
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
//
//        // when
//        eventService.unregisterUserFromEvent(username, eventId);
//
//        // then
//        verify(userRepository).findByUsername(username);
//        verify(eventRepository).findById(eventId);
//        verify(userRepository).save(user);
//
//        assertTrue(user.getRegisteredEvents().isEmpty());
//    }
//
//    @Test
//    void unregisterUserFromEvent_shouldThrowException_whenUserNotRegistered() {
//        // given
//        Long eventId = 1L;
//        String username = "user1";
//
//        Event event = new Event("Test Event", "Test Description", LocalDate.now().plusDays(1));
//        event.setId(eventId);
//
//        User user = new User();
//        user.setId(1L);
//        user.setUsername(username);
//        // user is NOT registered for the event
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
//
//        // when + then
//        assertThrows(IllegalStateException.class,
//                () -> eventService.unregisterUserFromEvent(username, eventId));
//
//        verify(userRepository, never()).save(user);
//        assertTrue(user.getRegisteredEvents().isEmpty());
//    }
//
//    @Test
//    void getUserEvents_shouldReturnUserEvents() {
//        // given
//        String username = "user1";
//
//        Event event1 = new Event("Event 1", "Desc 1", LocalDate.now().plusDays(1));
//        event1.setId(1L);
//
//        Event event2 = new Event("Event 2", "Desc 2", LocalDate.now().plusDays(2));
//        event2.setId(2L);
//
//        User user = new User();
//        user.setId(1L);
//        user.setUsername(username);
//        user.getRegisteredEvents().add(event1);
//        user.getRegisteredEvents().add(event2);
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//
//        // when
//        List<Event> result = eventService.getUserEvents(username);
//
//        // then
//        verify(userRepository).findByUsername(username);
//
//        assertEquals(2, result.size());
//        assertTrue(result.contains(event1));
//        assertTrue(result.contains(event2));
//    }
//
//    @Test
//    void getUserEvents_shouldThrowException_whenUserNotFound() {
//        // given
//        String username = "unknownUser";
//        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
//
//        // when + then
//        assertThrows(NoSuchElementException.class,
//                () -> eventService.getUserEvents(username));
//    }
//
//}