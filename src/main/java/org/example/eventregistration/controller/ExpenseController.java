package org.example.eventregistration.controller;

import org.example.eventregistration.service.ExpenseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Controller managing the shared ledger and expense tracking within a group.
 */
@Controller
@RequestMapping("/groups/{groupId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public String showExpenses(@PathVariable Long groupId, Model model) {
        model.addAttribute("groupId", groupId);
        model.addAttribute("expenses", expenseService.getGroupExpenses(groupId));
        model.addAttribute("totalSpent", expenseService.calculateGroupTotal(groupId));
        return "expenses";
    }


    @PostMapping
    public String addExpense(@PathVariable Long groupId,
                             @RequestParam String description,
                             @RequestParam BigDecimal amount,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {

        expenseService.addExpense(groupId, userDetails.getUsername(), description, amount);
        redirectAttributes.addFlashAttribute("message", "Expense added successfully!");
        return "redirect:/groups/" + groupId + "/expenses";
    }
}