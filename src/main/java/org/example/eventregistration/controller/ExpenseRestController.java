package org.example.eventregistration.controller;

import org.example.eventregistration.dto.AddExpenseRequest;
import org.example.eventregistration.dto.ExpenseResponseDTO;
import org.example.eventregistration.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
public class ExpenseRestController {

    private final ExpenseService expenseService;

    public ExpenseRestController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseResponseDTO> getGroupExpenses(@PathVariable Long groupId) {
        return expenseService.getGroupExpenses(groupId)
                .stream()
                .map(e -> new ExpenseResponseDTO(
                        e.getId(),
                        e.getDescription(),
                        e.getAmount(),
                        e.getDate(),
                        e.getPaidBy().getUsername()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getGroupTotal(@PathVariable Long groupId) {
        return ResponseEntity.ok(expenseService.calculateGroupTotal(groupId));
    }

    @PostMapping
    public ResponseEntity<String> addExpense(@PathVariable Long groupId, @RequestBody AddExpenseRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            expenseService.addExpense(groupId, username, request.getDescription(), request.getAmount());
            return ResponseEntity.ok("Expense added successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}