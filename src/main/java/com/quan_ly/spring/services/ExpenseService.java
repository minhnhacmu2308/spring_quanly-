package com.quan_ly.spring.services;

import com.quan_ly.spring.models.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    List<Expense> getAllExpense();
    Optional<Expense> getExpenseById(Long id);
    Expense createExpense(Expense expense);
}
