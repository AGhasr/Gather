package org.example.eventregistration.controller;

import org.example.eventregistration.dto.AddMemberRequest;
import org.example.eventregistration.dto.CreateGroupRequest;
import org.example.eventregistration.dto.GroupResponseDTO;
import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.User;
import org.example.eventregistration.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GroupRestController {

    private final GroupService groupService;

    public GroupRestController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupResponseDTO> getMyGroups() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return groupService.getMyGroups(username)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<String> createGroup(@RequestBody CreateGroupRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        groupService.createGroup(request.getName(), username);
        return ResponseEntity.ok("Group created successfully.");
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<String> addMember(@PathVariable Long groupId, @RequestBody AddMemberRequest request) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            groupService.addMember(groupId, request.getUsername(), adminUsername);
            return ResponseEntity.ok("Member added successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private GroupResponseDTO convertToDTO(Group group) {
        List<String> memberNames = group.getMembers().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        return new GroupResponseDTO(
                group.getId(),
                group.getName(),
                group.getAdmin().getUsername(),
                memberNames
        );
    }
}