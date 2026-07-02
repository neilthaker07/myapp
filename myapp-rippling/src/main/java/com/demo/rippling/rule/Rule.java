package com.demo.rippling.rule;

import com.demo.rippling.model.Expense;

import java.util.Optional;

/**
 * @FunctionalInterface — single method contract.
 * Simple rules can be passed as lambdas; complex rules implement this interface.
 * Returns empty Optional if the expense passes, or a human-readable rejection reason if violated.
 */
@FunctionalInterface
public interface Rule {
    Optional<String> evaluate(Expense expense);
}
