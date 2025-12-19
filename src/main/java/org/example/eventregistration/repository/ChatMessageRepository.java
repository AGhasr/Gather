package org.example.eventregistration.repository;

import org.example.eventregistration.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Find messages for a group and sort them so oldest is first (top of chat)
    List<ChatMessage> findByGroupIdOrderByTimestampAsc(Long groupId);
}