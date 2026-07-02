package com.demo.rippling.model;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class EvaluatedExpense {

    String expenseId;
    String tripId;
    EvaluationStatus status;
    BigDecimal amount;
    String reason; // null when APPROVED; populated with human-readable message when REJECTED

    public static EvaluatedExpense approved(Expense expense) {
        return new EvaluatedExpense(
                expense.getExpenseId(), expense.getTripId(),
                EvaluationStatus.APPROVED, expense.getAmount(), null);
    }

    public static EvaluatedExpense rejected(Expense expense, String reason) {
        return new EvaluatedExpense(
                expense.getExpenseId(), expense.getTripId(),
                EvaluationStatus.REJECTED, expense.getAmount(), reason);
    }
}
