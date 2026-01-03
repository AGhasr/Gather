package org.example.eventregistration.controller;

import org.example.eventregistration.dto.DebtDTO;
import org.example.eventregistration.service.DebtService;
import org.example.eventregistration.service.GroupService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DebtController {

    private final DebtService debtService;
    private final GroupService groupService;

    public DebtController(DebtService debtService, GroupService groupService) {
        this.debtService = debtService;
        this.groupService = groupService;
    }

    @GetMapping("/debts")
    public String showDebts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        var myGroups = groupService.getMyGroups(username);

        List<DebtDTO> myDebtsToPay = new ArrayList<>();
        List<DebtDTO> debtsOwedToMe = new ArrayList<>();

        for (var group : myGroups) {
            List<DebtDTO> groupDebts = debtService.calculateGroupDebts(group.getId());

            for (DebtDTO debt : groupDebts) {
                if (debt.getFromUser().equals(username)) {
                    myDebtsToPay.add(debt);
                } else if (debt.getToUser().equals(username)) {
                    debtsOwedToMe.add(debt);
                }
            }
        }

        model.addAttribute("toPay", myDebtsToPay);
        model.addAttribute("owedToMe", debtsOwedToMe);

        return "debts";
    }

    @PostMapping("/debts/settle")
    public String settleDebt(@RequestParam Long groupId,
                             @RequestParam String payee,
                             @RequestParam BigDecimal amount,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {

        debtService.settleUp(groupId, userDetails.getUsername(), payee, amount);
        redirectAttributes.addFlashAttribute("message", "Payment recorded! Debt settled.");
        return "redirect:/debts";
    }
}