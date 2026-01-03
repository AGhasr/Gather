package org.example.eventregistration.service;

import org.example.eventregistration.dto.DebtDTO;
import org.example.eventregistration.model.Expense;
import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.Settlement;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.ExpenseRepository;
import org.example.eventregistration.repository.GroupRepository;
import org.example.eventregistration.repository.SettlementRepository;
import org.example.eventregistration.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class DebtService {

    private final ExpenseRepository expenseRepo;
    private final SettlementRepository settlementRepo;
    private final GroupRepository groupRepo;
    private final UserRepository userRepo;

    public DebtService(ExpenseRepository expenseRepo, SettlementRepository settlementRepo, GroupRepository groupRepo, UserRepository userRepo) {
        this.expenseRepo = expenseRepo;
        this.settlementRepo = settlementRepo;
        this.groupRepo = groupRepo;
        this.userRepo = userRepo;
    }

    /**
     * Calculates the net total balance for a specific user across all their groups.
     * Positive = They are owed money. Negative = They owe money.
     */
    public BigDecimal getUserGlobalBalance(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (Group group : user.getGroups()) {
            totalBalance = totalBalance.add(calculateUserBalanceInGroup(user, group));
        }
        return totalBalance;
    }


    public List<DebtDTO> calculateGroupDebts(Long groupId) {
        Group group = groupRepo.findById(groupId).orElseThrow();
        List<User> members = group.getMembers();

        Map<User, BigDecimal> balances = new HashMap<>();
        for (User member : members) {
            balances.put(member, calculateUserBalanceInGroup(member, group));
        }

        List<User> debtors = new ArrayList<>();
        List<User> creditors = new ArrayList<>();

        for (Map.Entry<User, BigDecimal> entry : balances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) debtors.add(entry.getKey());
            else if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) creditors.add(entry.getKey());
        }

        List<DebtDTO> debts = new ArrayList<>();
        int i = 0;
        int j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            User debtor = debtors.get(i);
            User creditor = creditors.get(j);

            BigDecimal debtorAmount = balances.get(debtor).abs();
            BigDecimal creditorAmount = balances.get(creditor);

            BigDecimal settleAmount = debtorAmount.min(creditorAmount);

            debts.add(new DebtDTO(debtor.getUsername(), creditor.getUsername(), settleAmount, group.getId(), group.getName()));

            balances.put(debtor, balances.get(debtor).add(settleAmount));
            balances.put(creditor, balances.get(creditor).subtract(settleAmount));

            if (balances.get(debtor).compareTo(BigDecimal.ZERO) == 0) i++;
            if (balances.get(creditor).compareTo(BigDecimal.ZERO) == 0) j++;
        }

        return debts;
    }

    @Transactional
    public void settleUp(Long groupId, String payerUsername, String payeeUsername, BigDecimal amount) {
        Group group = groupRepo.findById(groupId).orElseThrow();
        User payer = userRepo.findByUsername(payerUsername).orElseThrow();
        User payee = userRepo.findByUsername(payeeUsername).orElseThrow();

        Settlement settlement = new Settlement(payer, payee, group, amount);
        settlementRepo.save(settlement);
    }

    private BigDecimal calculateUserBalanceInGroup(User user, Group group) {
        List<Expense> expenses = expenseRepo.findByGroupIdOrderByDateDesc(group.getId());
        List<Settlement> settlements = settlementRepo.findByGroupId(group.getId());
        List<User> members = group.getMembers();
        int memberCount = members.size();

        if (memberCount == 0) return BigDecimal.ZERO;

        BigDecimal myBalance = BigDecimal.ZERO;

        for (Expense expense : expenses) {
            BigDecimal amount = expense.getAmount();

            BigDecimal baseShare = amount.divide(BigDecimal.valueOf(memberCount), 2, RoundingMode.DOWN);

            BigDecimal totalCalculated = baseShare.multiply(BigDecimal.valueOf(memberCount));
            BigDecimal remainder = amount.subtract(totalCalculated);

            int pennies = remainder.movePointRight(2).intValue();

            BigDecimal myLiability = baseShare;

            int myIndex = members.indexOf(user);
            if (myIndex < pennies) {
                myLiability = myLiability.add(new BigDecimal("0.01"));
            }

            if (expense.getPaidBy().equals(user)) {
                myBalance = myBalance.add(amount);
            }

            myBalance = myBalance.subtract(myLiability);
        }

        BigDecimal settlementsGiven = settlements.stream()
                .filter(s -> s.getPayer().equals(user))
                .map(Settlement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal settlementsReceived = settlements.stream()
                .filter(s -> s.getPayee().equals(user))
                .map(Settlement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return myBalance.add(settlementsGiven).subtract(settlementsReceived);
    }
}