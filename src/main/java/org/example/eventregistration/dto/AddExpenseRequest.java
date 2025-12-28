package org.example.eventregistration.dto;

import java.math.BigDecimal;

public class AddExpenseRequest {
    private String description;
    private BigDecimal amount;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}