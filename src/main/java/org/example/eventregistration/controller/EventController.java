package org.example.eventregistration.controller;

import org.example.eventregistration.model.Event;
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

import java.time.format.DateTimeFormatter;
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
     * Entry point for the application dashboard.
     * Fetches active and registered events for the authenticated session.
     */
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            model.addAttribute("events", eventService.getActiveEventsForUser(userDetails.getUsername()));
            model.addAttribute("registeredEvents", eventService.getUserEvents(userDetails.getUsername()));
        } else {
            model.addAttribute("events", List.of());
            model.addAttribute("registeredEvents", List.of());
        }
        return "events";
    }

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

    @GetMapping("/my-events")
    public String myEvents(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("myEvents", eventService.getUserEvents(userDetails.getUsername()));
        return "my-events";
    }

    @GetMapping("/events/new")
    public String createForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("event", new Event());
        model.addAttribute("groups", groupService.getMyGroups(userDetails.getUsername()));
        return "event-form";
    }

    @PostMapping("/events/new")
    public String createEvent(@ModelAttribute Event event,
                              @RequestParam Long groupId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        eventService.createEventWithGroup(event, groupId, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("message", "Event created successfully!");
        return "redirect:/";
    }

    @PostMapping("/admin/delete/{eventId}")
    public String deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return "redirect:/";
    }

    @GetMapping("/history")
    public String history(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("archivedEvents", eventService.getArchivedEventsForUser(userDetails.getUsername()));
        return "history";
    }

    @PostMapping("/event/{id}/archive")
    public String archiveEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            eventService.archiveEvent(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("message", "Event moved to history!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    /**
     * Generates an iCalendar (.ics) file for integration with external calendar applications.
     */
    @GetMapping("/events/{id}/export")
    public ResponseEntity<String> exportToCalendar(@PathVariable Long id) {
        Event event = eventService.getEventById(id);

        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\n")
                .append("VERSION:2.0\n")
                .append("PRODID:-//Gather//SocialTravelPlanner//EN\n")
                .append("BEGIN:VEVENT\n")
                .append("UID:").append(event.getId()).append("@gather.app\n")
                .append("DTSTAMP:").append(DateTimeFormatter.BASIC_ISO_DATE.format(java.time.LocalDate.now())).append("T000000Z\n");

        String cleanDate = DateTimeFormatter.BASIC_ISO_DATE.format(event.getDate());
        ics.append("DTSTART;VALUE=DATE:").append(cleanDate).append("\n")
                .append("SUMMARY:").append(event.getTitle()).append("\n")
                .append("DESCRIPTION:").append(event.getDescription()).append(" (Group: ").append(event.getGroup().getName()).append(")\n")
                .append("END:VEVENT\n")
                .append("END:VCALENDAR");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"event-" + id + ".ics\"")
                .contentType(MediaType.parseMediaType("text/calendar"))
                .body(ics.toString());
    }
}