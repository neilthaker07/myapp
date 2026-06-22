package com.demo.expense.rule;

import com.demo.expense.model.Expense;

public interface Rule {
    boolean isViolated(Expense expense);
    String describe();
}
