package com.demo.expense.rule;

import com.demo.expense.model.Expense;

import java.math.BigDecimal;
import java.util.List;

public enum TripRuleField {

    TOTAL_AMOUNT {
        @Override
        public String extract(List<Expense> expenses) {
            return expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .toPlainString();
        }
    },
    EXPENSE_COUNT {
        @Override
        public String extract(List<Expense> expenses) {
            return String.valueOf(expenses.size());
        }
    };

    public abstract String extract(List<Expense> expenses);
}
