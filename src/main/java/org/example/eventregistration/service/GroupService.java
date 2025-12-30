package org.example.eventregistration.service;

import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.GroupRepository;
import org.example.eventregistration.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, EmailService emailService) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public List<Group> getMyGroups(String username) {
        return groupRepository.findAllByMember(username);
    }

    @Transactional
    public void createGroup(String groupName, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Group group = new Group(groupName, user);
        groupRepository.save(group);
    }

    /**
     * Adds a new member to an existing group.
     * Restricted to the Group Administrator.
     * Triggers an email notification upon success.
     */
    @Transactional
    public void addMember(Long groupId, String newMemberUsername, String adminUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!group.getAdmin().getUsername().equals(adminUsername)) {
            throw new IllegalStateException("Only the admin can add members.");
        }

        User newMember = userRepository.findByUsername(newMemberUsername)
                .orElseThrow(() -> new IllegalArgumentException("User '" + newMemberUsername + "' not found"));

        if (group.getMembers().contains(newMember)) {
            throw new IllegalArgumentException("User is already in the group!");
        }

        group.getMembers().add(newMember);
        groupRepository.save(group);

        if (newMember.getEmail() != null && !newMember.getEmail().isEmpty()) {
            emailService.sendGroupNotification(newMember.getEmail(), group.getName(), adminUsername);
        }
    }

    @Transactional
    public String regenerateInviteCode(Long groupId, String adminUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!group.getAdmin().getUsername().equals(adminUsername)) {
            throw new IllegalStateException("Only admin can manage invite codes.");
        }

        group.setInviteCode(UUID.randomUUID().toString());
        groupRepository.save(group);
        return group.getInviteCode();
    }

    @Transactional
    public Group joinByInviteCode(String code, String username) {
        Group group = groupRepository.findByInviteCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite link"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (group.getMembers().contains(user)) {
            // User is already in group.
            return group;
        }

        group.getMembers().add(user);
        groupRepository.save(group);

        // TODO: Send email notification to Admin that someone joined
        return group;
    }
}