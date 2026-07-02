package com.demo.rippling.rule;

import com.demo.rippling.model.Expense;

import java.util.Optional;

/**
 * Stateful evaluator for a single trip. Called once per expense in sequence.
 * Maintains a running total internally — returns a violation reason the moment
 * the current expense pushes the total over the threshold.
 *
 * A fresh instance must be created per trip via TripRule.newEvaluator()
 * so running state never leaks between trips.
 */
@FunctionalInterface
public interface TripRuleEvaluator {
    Optional<String> check(Expense expense);
}
