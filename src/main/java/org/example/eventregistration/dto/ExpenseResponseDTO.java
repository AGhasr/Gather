package org.example.eventregistration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseResponseDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String paidBy;

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public ExpenseResponseDTO(Long id, String description, BigDecimal amount, LocalDate date, String paidBy) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.paidBy = paidBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }
}
