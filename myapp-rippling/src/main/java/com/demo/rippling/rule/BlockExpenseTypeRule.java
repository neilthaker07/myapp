package com.demo.rippling.rule;

import com.demo.rippling.model.Expense;
import lombok.Value;

import java.util.Optional;

// "No airfare expenses" / "No entertainment expenses"
// e.g. BlockExpenseTypeRule("airfare") + expense(type="airfare") → "Expense type 'airfare' is not allowed"
@Value
public class BlockExpenseTypeRule implements Rule {

    String blockedType;

    @Override
    public Optional<String> evaluate(Expense expense) {
        return expense.getExpenseType().equalsIgnoreCase(blockedType)
                ? Optional.of("Expense type '" + blockedType + "' is not allowed on this card")
                : Optional.empty();
    }
}
