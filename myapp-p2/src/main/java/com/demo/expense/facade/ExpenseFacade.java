package com.demo.expense.facade;

import com.demo.expense.dto.CompositeRuleRequest;
import com.demo.expense.model.Expense;
import com.demo.expense.model.ExpenseCategory;
import com.demo.expense.model.ReimbursementResult;
import com.demo.expense.rule.AndRule;
import com.demo.expense.rule.BudgetRule;
import com.demo.expense.rule.ExpenseRule;
import com.demo.expense.rule.ExpenseTripRule;
import com.demo.expense.rule.NotRule;
import com.demo.expense.rule.OrRule;
import com.demo.expense.rule.Rule;
import com.demo.expense.rule.RuleField;
import com.demo.expense.rule.RuleOperator;
import com.demo.expense.rule.TripRuleField;
import com.demo.expense.service.IExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExpenseFacade {

    private final IExpenseService expenseService;

    public void addExpense(Expense expense) {
        expenseService.addExpense(expense);
    }

    public void addRule(RuleField field, RuleOperator operator, String value) {
        expenseService.addRule(new ExpenseRule(field, operator, value));
    }

    public void addTripRule(TripRuleField field, RuleOperator operator, String value) {
        expenseService.addTripRule(new ExpenseTripRule(field, operator, value));
    }

    public void addCompositeRule(CompositeRuleRequest request) {
        expenseService.addRule(buildRule(request));
    }

    private Rule buildRule(CompositeRuleRequest request) {
        return switch (request.getType().toUpperCase()) {
            case "SIMPLE" -> new ExpenseRule(request.getField(), request.getOperator(), request.getValue());
            case "AND"    -> new AndRule(request.getRules().stream().map(this::buildRule).toList());
            case "OR"     -> new OrRule(request.getRules().stream().map(this::buildRule).toList());
            case "NOT"    -> new NotRule(buildRule(request.getRule()));
            default -> throw new IllegalArgumentException("Unknown rule type: " + request.getType());
        };
    }

    public List<Expense> evaluateExpenses() {
        return expenseService.evaluateExpenses();
    }

    public Map<String, List<Expense>> evaluateTrips() {
        return expenseService.evaluateTrips();
    }

    public void addBudgetRule(ExpenseCategory category, BigDecimal limit) {
        expenseService.addBudgetRule(new BudgetRule(category, limit));
    }

    public Map<String, ReimbursementResult> calculateReimbursement() {
        return expenseService.calculateReimbursement();
    }
}
