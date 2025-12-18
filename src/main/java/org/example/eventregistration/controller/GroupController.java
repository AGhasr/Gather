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

@Controller
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/groups")
    public String myGroups(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("groups", groupService.getMyGroups(userDetails.getUsername()));
        return "groups"; // We will create this HTML next
    }

    @PostMapping("/groups/new")
    public String createGroup(@RequestParam String name, @AuthenticationPrincipal UserDetails userDetails) {
        groupService.createGroup(name, userDetails.getUsername());
        return "redirect:/groups";
    }

    @PostMapping("/groups/{id}/add")
    public String addMember(@PathVariable Long id,
                            @RequestParam String username,
                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            groupService.addMember(id, username, userDetails.getUsername());
        } catch (RuntimeException e) {
            // Ideally pass this error to the view, but for now we redirect
            System.out.println("Error adding member: " + e.getMessage());
        }
        return "redirect:/groups";
    }
}