package org.example.eventregistration.repository;

import org.example.eventregistration.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByGroupIdOrderByDateDesc(Long groupId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.paidBy.username = :username")
    BigDecimal sumTotalSpentByUser(@Param("username") String username);
}