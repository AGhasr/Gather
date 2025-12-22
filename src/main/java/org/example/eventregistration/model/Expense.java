package org.example.eventregistration.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private BigDecimal amount;
    private LocalDate date;

    @ManyToOne
    private Group group;

    @ManyToOne
    private User paidBy;

    public Expense() {}

    public Expense(String description, BigDecimal amount, Group group, User paidBy) {
        this.description = description;
        this.amount = amount;
        this.group = group;
        this.paidBy = paidBy;
        this.date = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public User getPaidBy() { return paidBy; }
    public void setPaidBy(User paidBy) { this.paidBy = paidBy; }
}