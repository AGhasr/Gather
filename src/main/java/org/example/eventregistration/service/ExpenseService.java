package org.example.eventregistration.service;

import org.example.eventregistration.model.Expense;
import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.ExpenseRepository;
import org.example.eventregistration.repository.GroupRepository;
import org.example.eventregistration.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, GroupRepository groupRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public List<Expense> getGroupExpenses(Long groupId) {
        return expenseRepository.findByGroupIdOrderByDateDesc(groupId);
    }

    @Transactional
    public void addExpense(Long groupId, String username, String description, BigDecimal amount) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isMember = group.getMembers().contains(user);
        if (!isMember) {
            throw new IllegalStateException("You must be a member of the group to add expenses.");
        }

        Expense expense = new Expense(description, amount, group, user);
        expenseRepository.save(expense);
    }

    public BigDecimal calculateGroupTotal(Long groupId) {
        return expenseRepository.findByGroupIdOrderByDateDesc(groupId).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}