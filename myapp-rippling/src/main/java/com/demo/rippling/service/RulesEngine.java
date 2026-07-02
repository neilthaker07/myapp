package com.demo.rippling.service;

import com.demo.rippling.model.EvaluatedExpense;
import com.demo.rippling.model.Expense;
import com.demo.rippling.rule.Rule;
import com.demo.rippling.rule.TripRule;
import com.demo.rippling.rule.TripRuleEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RulesEngine {

    public List<EvaluatedExpense> evaluateRules(List<Rule> rules, List<Expense> expenses) {
        return evaluateRules(rules, List.of(), expenses);
    }

    public List<EvaluatedExpense> evaluateRules(List<Rule> rules, List<TripRule> tripRules, List<Expense> expenses) {
        // Group by tripId; LinkedHashMap preserves the order trips first appear in input
        Map<String, List<Expense>> byTrip = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getTripId, LinkedHashMap::new, Collectors.toList()));

        // Evaluate each trip sequentially, collect into a lookup map
        Map<String, EvaluatedExpense> resultById = new LinkedHashMap<>();
        byTrip.forEach((tripId, tripExpenses) ->
                evaluateTrip(rules, tripRules, tripExpenses)
                        .forEach(r -> resultById.put(r.getExpenseId(), r)));

        // Return in original input order
        return expenses.stream()
                .map(e -> resultById.get(e.getExpenseId()))
                .toList();
    }

    /**
     * Iterates expenses in sequence. Per-expense rules run first.
     * Trip rules maintain running totals via fresh evaluators — the moment a trip rule fires,
     * that expense and all subsequent in the trip are REJECTED.
     *
     * Per-expense rejected expenses do NOT count toward trip totals:
     * if the card blocks an expense (e.g. airfare), it was never charged, so it
     * shouldn't consume the trip budget.
     */
    private List<EvaluatedExpense> evaluateTrip(List<Rule> rules, List<TripRule> tripRules,
                                                 List<Expense> tripExpenses) {
        // Fresh evaluator per rule — each trip starts with zero running totals
        List<TripRuleEvaluator> evaluators = tripRules.stream()
                .map(TripRule::newEvaluator)
                .toList();

        List<EvaluatedExpense> results = new ArrayList<>();
        boolean tripViolated = false;
        String tripViolationReason = null;

        for (Expense expense : tripExpenses) {

            // Step 1: per-expense rules (independent of trip state)
            Optional<String> expenseViolation = rules.stream()
                    .map(rule -> rule.evaluate(expense))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();

            if (expenseViolation.isPresent()) {
                // Blocked by per-expense rule — does NOT count toward trip running totals
                log.info("REJECTED expense={} (expense rule) reason={}", expense.getExpenseId(), expenseViolation.get());
                results.add(EvaluatedExpense.rejected(expense, expenseViolation.get()));
                continue;
            }

            // Step 2: if trip already violated, reject all subsequent without re-checking
            if (tripViolated) {
                log.info("REJECTED expense={} (trip already violated) reason={}", expense.getExpenseId(), tripViolationReason);
                results.add(EvaluatedExpense.rejected(expense, tripViolationReason));
                continue;
            }

            // Step 3: feed to trip evaluators — first one that fires rejects this expense
            // e.g. expense 9 in a 9×$250 trip pushes total to $2250 → fires here
            Optional<String> tripViolation = evaluators.stream()
                    .map(ev -> ev.check(expense))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();

            if (tripViolation.isPresent()) {
                tripViolated = true;
                tripViolationReason = tripViolation.get();
                log.info("REJECTED expense={} (trip rule triggered) reason={}", expense.getExpenseId(), tripViolationReason);
                results.add(EvaluatedExpense.rejected(expense, tripViolationReason));
            } else {
                log.info("APPROVED expense={}", expense.getExpenseId());
                results.add(EvaluatedExpense.approved(expense));
            }
        }

        return results;
    }
}
