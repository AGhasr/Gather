package org.example.eventregistration.service;

import org.example.eventregistration.dto.CreatePollRequest;
import org.example.eventregistration.model.*;
import org.example.eventregistration.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ChatService {

    private final ChatMessageRepository chatRepo;
    private final PollRepository pollRepo;
    private final UserRepository userRepo;

    public ChatService(ChatMessageRepository chatRepo, PollRepository pollRepo, UserRepository userRepo, GroupRepository groupRepo) {
        this.chatRepo = chatRepo;
        this.pollRepo = pollRepo;
        this.userRepo = userRepo;
    }

    public ChatMessage saveTextMessage(Long groupId, ChatMessage message) {
        message.setGroupId(groupId);
        message.setTimestamp(LocalDateTime.now());
        message.setType(ChatMessage.MessageType.CHAT);
        return chatRepo.save(message);
    }

    @Transactional
    public ChatMessage createPoll(Long groupId, String senderUsername, CreatePollRequest request) {
        Poll poll = new Poll(request.getQuestion());

        for (String optionText : request.getOptions()) {
            PollOption option = new PollOption(optionText, poll);
            poll.getOptions().add(option);
        }

        ChatMessage message = new ChatMessage();
        message.setGroupId(groupId);
        message.setSender(senderUsername);
        message.setContent("Poll: " + request.getQuestion());
        message.setTimestamp(LocalDateTime.now());
        message.setType(ChatMessage.MessageType.POLL);
        message.setPoll(poll);
        poll.setMessage(message);

        return chatRepo.save(message);
    }

    @Transactional
    public ChatMessage castVote(String username, Long pollId, Long optionId) {
        Poll poll = pollRepo.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        for (PollOption option : poll.getOptions()) {
            option.getVotes().removeIf(vote -> vote.getUser().getUsername().equals(username));
        }

        PollOption selectedOption = poll.getOptions().stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Option not found"));

        PollVote vote = new PollVote(user, selectedOption);
        selectedOption.getVotes().add(vote);

        pollRepo.save(poll);

        return poll.getMessage();
    }
}