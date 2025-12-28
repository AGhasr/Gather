package org.example.eventregistration.dto;

import java.util.List;

public class GroupResponseDTO {
    private Long id;
    private String name;
    private String admin;
    private List<String> members;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public GroupResponseDTO(Long id, String name, String admin, List<String> members) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAdmin() {
        return admin;
    }

    public List<String> getMembers() {
        return members;
    }
}
