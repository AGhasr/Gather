package org.example.eventregistration.controller;

import org.example.eventregistration.model.ChatMessage;
import org.example.eventregistration.repository.ChatMessageRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatMessageRepository chatRepo;

    public ChatController(ChatMessageRepository chatRepo) {
        this.chatRepo = chatRepo;
    }

    /**
     * Renders the chat view and loads the message history for a specific group.
     */
    @GetMapping("/groups/{groupId}/chat")
    public String chatPage(@PathVariable Long groupId, Model model) {
        model.addAttribute("groupId", groupId);
        model.addAttribute("history", chatRepo.findByGroupIdOrderByTimestampAsc(groupId));
        return "chat";
    }

    /**
     * Intercepts messages sent via WebSocket, persists them to the database,
     * and broadcasts the payload to all subscribers of the specific group topic.
     */
    @MessageMapping("/chat/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public ChatMessage sendMessage(@DestinationVariable Long groupId, @Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return chatRepo.save(message);
    }
}