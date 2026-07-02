package com.demo.rippling.rule;

import com.demo.rippling.model.Expense;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Optional;

// "No expenses over $250"
// e.g. MaxAmountRule(250) + expense($300) → "Expense $300 exceeds per-expense limit of $250"
@Value
public class MaxAmountRule implements Rule {

    BigDecimal maxAmount;

    @Override
    public Optional<String> evaluate(Expense expense) {
        return expense.getAmount().compareTo(maxAmount) > 0
                ? Optional.of(String.format(
                        "Expense $%s exceeds per-expense limit of $%s",
                        expense.getAmount().toPlainString(), maxAmount.toPlainString()))
                : Optional.empty();
    }
}
