package org.example.eventregistration.controller;

import org.example.eventregistration.dto.CreatePollRequest;
import org.example.eventregistration.dto.VoteRequest;
import org.example.eventregistration.model.ChatMessage;
import org.example.eventregistration.repository.ChatMessageRepository;
import org.example.eventregistration.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatMessageRepository chatRepo;
    private final ChatService chatService;

    public ChatController(ChatMessageRepository chatRepo, ChatService chatService) {
        this.chatRepo = chatRepo;
        this.chatService = chatService;
    }

    @GetMapping("/groups/{groupId}/chat")
    public String chatPage(@PathVariable Long groupId, Model model) {
        model.addAttribute("groupId", groupId);
        model.addAttribute("history", chatRepo.findByGroupIdOrderByTimestampAsc(groupId));
        return "chat";
    }

    @MessageMapping("/chat/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public ChatMessage sendMessage(@DestinationVariable Long groupId, @Payload ChatMessage message) {
        return chatService.saveTextMessage(groupId, message);
    }

    @MessageMapping("/chat/{groupId}/poll/create")
    @SendTo("/topic/group/{groupId}")
    public ChatMessage createPoll(@DestinationVariable Long groupId,
                                  @Payload CreatePollRequest request,
                                  Principal principal) {
        return chatService.createPoll(groupId, principal.getName(), request);
    }

    @MessageMapping("/chat/{groupId}/poll/vote")
    @SendTo("/topic/group/{groupId}")
    public ChatMessage voteOnPoll(@DestinationVariable Long groupId,
                                  @Payload VoteRequest request,
                                  Principal principal) {
        return chatService.castVote(principal.getName(), request.getPollId(), request.getOptionId());
    }
}