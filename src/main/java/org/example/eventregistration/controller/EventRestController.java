package org.example.eventregistration.controller;

import org.example.eventregistration.dto.EventDTO;
import org.example.eventregistration.model.Event;
import org.example.eventregistration.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    private final EventService eventService;

    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/events - List all upcoming events
    @GetMapping
    public List<EventDTO> getAllUpcomingEvents() {
        return eventService.getUpcomingEvents()
                .stream()
                .map(e -> new EventDTO(
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getDate()
                ))
                .toList();
    }

    // POST /api/events/new?groupId=1 - Create a new event via API
    // THIS WAS MISSING
    @PostMapping("/new")
    public ResponseEntity<?> createEvent(@RequestBody EventDTO eventDto, @RequestParam Long groupId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Event event = new Event();
            event.setTitle(eventDto.getTitle());
            event.setDescription(eventDto.getDescription());
            event.setDate(eventDto.getDate());

            eventService.createEventWithGroup(event, groupId, username);
            return ResponseEntity.ok("Event created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /api/events/register/{eventId}
    @PostMapping("/register/{eventId}")
    public ResponseEntity<String> registerForEvent(@PathVariable Long eventId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            eventService.registerUserForEvent(username, eventId);
            return ResponseEntity.ok("Registered successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/events/mine
    @GetMapping("/mine")
    public List<EventDTO> getMyRegisteredEvents() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return eventService.getUserEvents(username)
                .stream()
                .map(e -> new EventDTO(
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getDate()
                ))
                .toList();
    }

    // POST /api/events/unregister/{eventId}
    @PostMapping("/unregister/{eventId}")
    public ResponseEntity<String> unregisterFromEvent(@PathVariable Long eventId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            eventService.unregisterUserFromEvent(username, eventId);
            return ResponseEntity.ok("Unregistered successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}