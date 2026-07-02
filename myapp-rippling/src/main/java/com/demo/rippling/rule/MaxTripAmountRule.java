package com.demo.rippling.rule;

import com.demo.rippling.model.Expense;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

// "A trip cannot exceed $2000 in total expenses"
// Sequential: expenses 1-8 at $250 each = $2000 (passes, rule is >).
// Expense 9 pushes to $2250 → first violation. Expense 9 and onward are REJECTED.
@RequiredArgsConstructor
public class MaxTripAmountRule implements TripRule {

    private final BigDecimal maxAmount;

    @Override
    public TripRuleEvaluator newEvaluator() {
        return new TripRuleEvaluator() {
            BigDecimal running = BigDecimal.ZERO;

            @Override
            public Optional<String> check(Expense expense) {
                running = running.add(expense.getAmount());
                return running.compareTo(maxAmount) > 0
                        ? Optional.of(String.format(
                                "Trip total $%s exceeds limit of $%s",
                                running.toPlainString(), maxAmount.toPlainString()))
                        : Optional.empty();
            }
        };
    }
}
