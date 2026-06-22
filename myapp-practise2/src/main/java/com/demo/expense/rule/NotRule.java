package com.demo.expense.rule;

import com.demo.expense.model.Expense;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotRule implements Rule {

    private final Rule rule;

    @Override
    public boolean isViolated(Expense expense) {
        return !rule.isViolated(expense);
    }

    @Override
    public String describe() {
        return "NOT(" + rule.describe() + ")";
    }
}
