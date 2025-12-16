package org.example.eventregistration.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a user who can register for events.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;
    private String role;

    @ManyToMany
    private List<Event> registeredEvents = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Group> groups = new ArrayList<>();

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public List<Group> getGroups() { return groups; }
    public void setGroups(List<Group> groups) { this.groups = groups; }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the list of events this user is registered for.
     */
    public List<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    public void setRegisteredEvents(List<Event> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }
}
