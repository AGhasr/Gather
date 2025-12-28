package org.example.eventregistration.service;

import org.example.eventregistration.model.Expense;
import org.example.eventregistration.model.Group;
import org.example.eventregistration.model.User;
import org.example.eventregistration.repository.ExpenseRepository;
import org.example.eventregistration.repository.GroupRepository;
import org.example.eventregistration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private UserRepository userRepository;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(expenseRepository, groupRepository, userRepository);
    }

    @Test
    void addExpense_shouldSaveExpense_WhenUserIsMember() {
        // given
        Long groupId = 1L;
        String username = "member";
        BigDecimal amount = BigDecimal.valueOf(50.00);

        User user = new User();
        user.setUsername(username);

        Group group = new Group();
        group.setId(groupId);
        group.getMembers().add(user); // Add user to group

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when
        expenseService.addExpense(groupId, username, "Dinner", amount);

        // then
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void addExpense_shouldThrow_WhenUserIsNotMember() {
        // given
        Long groupId = 1L;
        String username = "outsider";

        User user = new User();
        user.setUsername(username);
        Group group = new Group(); // Empty members list

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when & then
        assertThrows(IllegalStateException.class, () ->
                expenseService.addExpense(groupId, username, "Taxi", BigDecimal.TEN)
        );
    }

    @Test
    void calculateGroupTotal_shouldSumAmounts() {
        // given
        Long groupId = 1L;
        Expense e1 = new Expense();
        e1.setAmount(BigDecimal.valueOf(10.50));

        Expense e2 = new Expense();
        e2.setAmount(BigDecimal.valueOf(20.50));

        when(expenseRepository.findByGroupIdOrderByDateDesc(groupId))
                .thenReturn(List.of(e1, e2));

        // when
        BigDecimal total = expenseService.calculateGroupTotal(groupId);

        // then
        assertThat(total).isEqualTo(BigDecimal.valueOf(31.00));
    }
}