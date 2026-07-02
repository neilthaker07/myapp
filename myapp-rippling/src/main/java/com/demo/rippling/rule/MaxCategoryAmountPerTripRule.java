package com.demo.rippling.rule;

import com.demo.rippling.model.Expense;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

// "Total meal expenses cannot exceed $200 per trip"
// Only accumulates when expense_type matches. Non-meal expenses pass through without affecting the counter.
// e.g. meals $120 → running $120 (OK); meals $100 → running $220 > $200 → REJECTED from here.
@RequiredArgsConstructor
public class MaxCategoryAmountPerTripRule implements TripRule {

    private final String expenseType;
    private final BigDecimal maxAmount;

    @Override
    public TripRuleEvaluator newEvaluator() {
        return new TripRuleEvaluator() {
            BigDecimal running = BigDecimal.ZERO;

            @Override
            public Optional<String> check(Expense expense) {
                // Non-matching categories skip accumulation — only meals count toward meal limit
                if (!expense.getExpenseType().equalsIgnoreCase(expenseType)) return Optional.empty();

                running = running.add(expense.getAmount());
                return running.compareTo(maxAmount) > 0
                        ? Optional.of(String.format(
                                "Trip '%s' total $%s exceeds limit of $%s",
                                expenseType, running.toPlainString(), maxAmount.toPlainString()))
                        : Optional.empty();
            }
        };
    }
}
