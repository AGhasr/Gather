package org.example.eventregistration.service;

import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.GroupRepository;
import org.example.eventregistration.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public List<Group> getMyGroups(String username) {
        return groupRepository.findAllByMember(username);
    }

    @Transactional
    public void createGroup(String groupName, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        // Create group and set the creator as Admin
        Group group = new Group(groupName, user);

        // The constructor we wrote earlier adds the admin to members automatically,
        // but let's be safe and ensure the relationship is saved.
        groupRepository.save(group);
    }
    @Transactional
    public void addMember(Long groupId, String newMemberUsername, String adminUsername) {
        // 1. Find the group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        // 2. Security: Only the Admin can add people
        if (!group.getAdmin().getUsername().equals(adminUsername)) {
            throw new IllegalStateException("Only the admin can add members.");
        }

        // 3. Find the user to add
        User newMember = userRepository.findByUsername(newMemberUsername)
                .orElseThrow(() -> new IllegalArgumentException("User '" + newMemberUsername + "' not found"));

        // 4. Add them if not already there
        if (!group.getMembers().contains(newMember)) {
            group.getMembers().add(newMember);
            groupRepository.save(group); // Save the relationship
        }
    }
}