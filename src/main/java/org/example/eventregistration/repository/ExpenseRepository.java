package org.example.eventregistration.repository;

import org.example.eventregistration.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByGroupIdOrderByDateDesc(Long groupId);
}