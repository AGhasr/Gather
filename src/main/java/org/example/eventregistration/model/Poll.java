package org.example.eventregistration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polls")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PollOption> options = new ArrayList<>();

    @OneToOne(mappedBy = "poll")
    @JsonIgnore
    private ChatMessage message;

    public Poll() {}

    public Poll(String question) {
        this.question = question;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public List<PollOption> getOptions() { return options; }
    public void setOptions(List<PollOption> options) { this.options = options; }
    public ChatMessage getMessage() { return message; }
    public void setMessage(ChatMessage message) { this.message = message; }

    public int getTotalVotes() {
        if (options == null) {
            return 0;
        }
        return options.stream()
                .mapToInt(PollOption::getVoteCount)
                .sum();
    }
}