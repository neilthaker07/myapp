package com.demo.expense.rule;

import com.demo.expense.model.Expense;

import java.util.List;

public interface TripRule {
    boolean isViolated(List<Expense> tripExpenses);
    String describe();
}
