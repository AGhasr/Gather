package org.example.eventregistration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "poll_votes")
public class PollVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "option_id")
    @JsonIgnore
    private PollOption option;

    public PollVote() {}

    public PollVote(User user, PollOption option) {
        this.user = user;
        this.option = option;
    }

    @JsonProperty("username")
    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public PollOption getOption() { return option; }
    public void setOption(PollOption option) { this.option = option; }
}