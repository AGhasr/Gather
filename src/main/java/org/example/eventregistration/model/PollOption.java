package org.example.eventregistration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poll_options")
public class PollOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    @JsonIgnore
    private Poll poll;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)    private List<PollVote> votes = new ArrayList<>();

    public PollOption() {}

    public PollOption(String text, Poll poll) {
        this.text = text;
        this.poll = poll;
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public Poll getPoll() { return poll; }
    public List<PollVote> getVotes() { return votes; }

    public int getVoteCount() {
        return votes.size();
    }
}