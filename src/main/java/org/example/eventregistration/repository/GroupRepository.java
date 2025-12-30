package org.example.eventregistration.repository;

import org.example.eventregistration.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.username = :username")
    List<Group> findAllByMember(@Param("username") String username);
    Optional<Group> findByInviteCode(String inviteCode);
}