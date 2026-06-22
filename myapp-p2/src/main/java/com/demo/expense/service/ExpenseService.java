package com.demo.expense.service;

import com.demo.expense.model.Expense;
import com.demo.expense.model.ExpenseCategory;
import com.demo.expense.model.ExpenseReimbursement;
import com.demo.expense.model.ReimbursementResult;
import com.demo.expense.rule.BudgetRule;
import com.demo.expense.rule.Rule;
import com.demo.expense.rule.TripRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExpenseService implements IExpenseService {

    private final List<Expense> expenses = new CopyOnWriteArrayList<>();
    private final List<Rule> rules = new CopyOnWriteArrayList<>();
    private final List<TripRule> tripRules = new CopyOnWriteArrayList<>();
    // ConcurrentHashMap for O(1) lookup per category during reimbursement
    private final ConcurrentHashMap<ExpenseCategory, BigDecimal> budgetRules = new ConcurrentHashMap<>();

    @Override
    public void addExpense(Expense expense) {
        expenses.add(expense);
        log.info("Added expense {} for employee {} amount {}", expense.getId(), expense.getEmployeeId(), expense.getAmount());
    }

    @Override
    public void addRule(Rule rule) {
        rules.add(rule);
        log.info("Added rule: {}", rule.describe());
    }

    @Override
    public void addTripRule(TripRule tripRule) {
        tripRules.add(tripRule);
        log.info("Added trip rule: {}", tripRule.describe());
    }

    @Override
    public List<Expense> evaluateExpenses() {
        return expenses.stream()
                .filter(expense -> rules.stream().anyMatch(rule -> rule.isViolated(expense)))
                .toList();
    }

    @Override
    public void addBudgetRule(BudgetRule budgetRule) {
        budgetRules.put(budgetRule.getCategory(), budgetRule.getLimit());
        log.info("Added budget rule: {} limit {}", budgetRule.getCategory(), budgetRule.getLimit());
    }

    @Override
    public Map<String, List<Expense>> evaluateTrips() {
        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getTripId))
                .entrySet().stream()
                .filter(entry -> tripRules.stream().anyMatch(rule -> rule.isViolated(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, ReimbursementResult> calculateReimbursement() {
        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getTripId))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> calculateTripReimbursement(entry.getKey(), entry.getValue())));
    }

    private ReimbursementResult calculateTripReimbursement(String tripId, List<Expense> tripExpenses) {
        List<ExpenseReimbursement> details = new ArrayList<>();

        // Group by category — budget is applied per category within the trip
        tripExpenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory))
                .forEach((category, categoryExpenses) -> {
                    BigDecimal limit = budgetRules.get(category);
                    if (limit == null) {
                        // No budget rule for this category → fully approved
                        categoryExpenses.forEach(e ->
                                details.add(new ExpenseReimbursement(e.getId(), e.getAmount(), e.getAmount(), BigDecimal.ZERO)));
                    } else {
                        applyBudget(categoryExpenses, limit, details);
                    }
                });

        BigDecimal totalApproved = details.stream().map(ExpenseReimbursement::getApproved).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRejected = details.stream().map(ExpenseReimbursement::getRejected).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReimbursementResult(tripId, totalApproved, totalRejected, details);
    }

    // Sequential loop — each expense's approval depends on remaining budget from prior expenses.
    // A stream reduce would obscure this dependency, so imperative loop is the right choice here.
    private void applyBudget(List<Expense> expenses, BigDecimal limit, List<ExpenseReimbursement> details) {
        BigDecimal remaining = limit;
        for (Expense expense : expenses) {
            BigDecimal approved;
            BigDecimal rejected;
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                // Budget exhausted — fully rejected
                approved = BigDecimal.ZERO;
                rejected = expense.getAmount();
            } else if (expense.getAmount().compareTo(remaining) <= 0) {
                // Fully within remaining budget
                approved = expense.getAmount();
                rejected = BigDecimal.ZERO;
                remaining = remaining.subtract(expense.getAmount());
            } else {
                // Overlapping — approved up to remaining, rest rejected
                approved = remaining;
                rejected = expense.getAmount().subtract(remaining);
                remaining = BigDecimal.ZERO;
            }
            details.add(new ExpenseReimbursement(expense.getId(), expense.getAmount(), approved, rejected));
        }
    }
}
