package org.example.eventregistration.dto;

import java.math.BigDecimal;

public class DebtDTO {
    private String fromUser;
    private String toUser;
    private BigDecimal amount;
    private Long groupId;
    private String groupName;

    public DebtDTO(String fromUser, String toUser, BigDecimal amount, Long groupId, String groupName) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public String getFromUser() { return fromUser; }
    public String getToUser() { return toUser; }
    public BigDecimal getAmount() { return amount; }
    public Long getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
}