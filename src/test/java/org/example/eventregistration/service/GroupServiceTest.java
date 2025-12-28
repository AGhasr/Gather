package org.example.eventregistration.service;

import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.GroupRepository;
import org.example.eventregistration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock private GroupRepository groupRepository;
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;

    private GroupService groupService;

    @BeforeEach
    void setUp() {
        groupService = new GroupService(groupRepository, userRepository, emailService);
    }

    @Test
    void createGroup_shouldCreateGroupWithAdminAsMember() {
        // given
        String username = "adminUser";
        User admin = new User();
        admin.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(admin));

        // when
        groupService.createGroup("Paris Trip", username);

        // then
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void addMember_shouldAddUser_WhenCallerIsAdmin() {
        // given
        Long groupId = 1L;
        String adminName = "admin";
        String newMemberName = "newUser";
        String newMemberEmail = "new@test.com";

        User admin = new User();
        admin.setUsername(adminName);

        User newMember = new User();
        newMember.setUsername(newMemberName);
        newMember.setEmail(newMemberEmail);

        Group group = new Group("Trip", admin); // admin is set here
        group.setMembers(new ArrayList<>());
        group.getMembers().add(admin); // ensure admin is in members list

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findByUsername(newMemberName)).thenReturn(Optional.of(newMember));

        // when
        groupService.addMember(groupId, newMemberName, adminName);

        // then
        assertThat(group.getMembers()).contains(newMember);
        verify(groupRepository).save(group);
        verify(emailService).sendGroupNotification(eq(newMemberEmail), anyString(), eq(adminName));
    }

    @Test
    void addMember_shouldThrow_WhenCallerIsNotAdmin() {
        // given
        Long groupId = 1L;
        User admin = new User();
        admin.setUsername("realAdmin");

        Group group = new Group("Trip", admin);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // when & then
        assertThrows(IllegalStateException.class, () ->
                groupService.addMember(groupId, "someUser", "fakeAdmin")
        );

        verify(groupRepository, never()).save(any());
    }

    @Test
    void addMember_shouldThrow_WhenUserAlreadyInGroup() {
        // given
        Long groupId = 1L;
        String adminName = "admin";
        String userName = "existingUser";

        User admin = new User();
        admin.setUsername(adminName);

        User existingUser = new User();
        existingUser.setUsername(userName);

        Group group = new Group("Trip", admin);
        group.getMembers().add(existingUser); // User already here

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(existingUser));

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                groupService.addMember(groupId, userName, adminName)
        );
    }
}