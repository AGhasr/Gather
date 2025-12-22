package org.example.eventregistration.controller;

import org.example.eventregistration.service.GroupService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for managing social group lifecycles, including creation
 * and membership management.
 */
@Controller
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Retrieves all groups associated with the currently authenticated user.
     */
    @GetMapping("/groups")
    public String myGroups(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("groups", groupService.getMyGroups(userDetails.getUsername()));
        return "groups";
    }
    
    @PostMapping("/groups/new")
    public String createGroup(@RequestParam String name,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        groupService.createGroup(name, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("message", "Group created successfully!");
        return "redirect:/groups";
    }

    /**
     * Invites a user to join an existing group.
     */
    @PostMapping("/groups/{id}/add")
    public String addMember(@PathVariable Long id,
                            @RequestParam String username,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        try {
            groupService.addMember(id, username, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("message", username + " has been added to the group.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Could not add user: " + e.getMessage());
        }
        return "redirect:/groups";
    }
}