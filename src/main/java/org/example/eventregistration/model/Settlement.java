package org.example.eventregistration.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @ManyToOne
    @JoinColumn(name = "payee_id", nullable = false)
    private User payee;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private BigDecimal amount;
    private LocalDateTime timestamp;

    public Settlement() {}

    public Settlement(User payer, User payee, Group group, BigDecimal amount) {
        this.payer = payer;
        this.payee = payee;
        this.group = group;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getPayer() { return payer; }
    public void setPayer(User payer) { this.payer = payer; }

    public User getPayee() { return payee; }
    public void setPayee(User payee) { this.payee = payee; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}