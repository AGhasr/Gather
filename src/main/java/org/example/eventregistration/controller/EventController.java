package org.example.eventregistration.controller;


import org.example.eventregistration.model.Event;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.EventRepository;
import org.example.eventregistration.repository.UserRepository;
import org.example.eventregistration.service.EventService;
import org.example.eventregistration.service.GroupService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;


@Controller
public class EventController {

    private final EventService eventService;
    private final GroupService groupService;

    public EventController(EventService eventService, GroupService groupService) {
        this.eventService = eventService;
        this.groupService = groupService;
    }


    /**
     * Handles registration of the authenticated user to a specific event.
     * Prevents duplicate registrations.
     */
    @PostMapping("/registerEvent/{id}")
    public String registerEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            eventService.registerUserForEvent(userDetails.getUsername(), id);
            redirectAttributes.addFlashAttribute("message", "Successfully registered!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * Cancels a user's registration for a specific event.
     */
    @PostMapping("/cancelRegistration/{id}")
    public String cancelRegistration(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        try {
            eventService.unregisterUserFromEvent(userDetails.getUsername(), id);
            redirectAttributes.addFlashAttribute("message", "Unregistered successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/";
    }


    /**
     * Displays the list of events that the authenticated user is registered for.
     */
    @GetMapping("/my-events")
    public String myEvents(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("myEvents", eventService.getUserEvents(userDetails.getUsername()));
        return "my-events";
    }


    /**
     * Displays the form for creating a new event (admin only).
     */
    @GetMapping("/events/new")
    public String createForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("event", new Event());
        // Fetch groups the USER belongs to (so they can pick one)
        model.addAttribute("groups", groupService.getMyGroups(userDetails.getUsername()));
        return "event-form";
    }

    /**
     * Handles the submission of a new event created by an admin.
     */
    @PostMapping("/events/new")
    public String createEvent(@ModelAttribute Event event,
                              @RequestParam Long groupId,
                              @AuthenticationPrincipal UserDetails userDetails) {

        // The service already checks if the user is allowed to add to this group
        eventService.createEventWithGroup(event, groupId, userDetails.getUsername());
        return "redirect:/";
    }

    /**
     * Deletes an event (admin only) and removes it from all users' registrations.
     */
    @PostMapping("/admin/delete/{eventId}")
    public String deleteEvent(@PathVariable Long eventId) {

        eventService.deleteEvent(eventId);

        return "redirect:/";
    }


    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            // FIXED: Call eventService, not eventRepository
            model.addAttribute("events", eventService.getActiveEventsForUser(userDetails.getUsername()));
            model.addAttribute("registeredEvents", eventService.getUserEvents(userDetails.getUsername()));
        } else {
            model.addAttribute("events", List.of());
            model.addAttribute("registeredEvents", List.of());
        }
        return "events";
    }

    @GetMapping("/history")
    public String history(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // FIXED: Call eventService
        model.addAttribute("archivedEvents", eventService.getArchivedEventsForUser(userDetails.getUsername()));
        return "history";
    }

    /**
     * Archives an event so it moves to history instead of being deleted.
     * Only the Group Admin can perform this action.
     */
    @PostMapping("/event/{id}/archive")
    public String archiveEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            // Call the service to handle logic and security check
            eventService.archiveEvent(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("message", "Event moved to history!");
        } catch (IllegalStateException e) {
            // Handle error (e.g. if user is not the admin)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/";
    }

    // ... inside EventController class ...

    @GetMapping("/events/{id}/export")
    public ResponseEntity<String> exportToCalendar(@PathVariable Long id) {
        Event event = eventService.getEventById(id); // You might need to add this getter to Service if missing, or use Repository directly

        // 1. Build the iCalendar (ICS) content manually
        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\n");
        ics.append("VERSION:2.0\n");
        ics.append("PRODID:-//Gather//SocialTravelPlanner//EN\n");

        ics.append("BEGIN:VEVENT\n");
        ics.append("UID:").append(event.getId()).append("@gather.app\n");
        ics.append("DTSTAMP:").append(java.time.format.DateTimeFormatter.BASIC_ISO_DATE.format(java.time.LocalDate.now())).append("T000000Z\n");

        // Handle Date: Since we only have LocalDate, we treat it as an All-Day Event
        // Format: YYYYMMDD (e.g., 20251225)
        String cleanDate = java.time.format.DateTimeFormatter.BASIC_ISO_DATE.format(event.getDate());
        ics.append("DTSTART;VALUE=DATE:").append(cleanDate).append("\n");

        // Summary (Title) & Description
        ics.append("SUMMARY:").append(event.getTitle()).append("\n");
        ics.append("DESCRIPTION:").append(event.getDescription()).append(" (Group: ").append(event.getGroup().getName()).append(")\n");

        ics.append("END:VEVENT\n");
        ics.append("END:VCALENDAR");

        // 2. Return as a downloadable file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"event-" + id + ".ics\"")
                .contentType(MediaType.parseMediaType("text/calendar"))
                .body(ics.toString());
    }

}
