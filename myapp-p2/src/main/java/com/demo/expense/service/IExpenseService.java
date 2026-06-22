package com.demo.expense.service;

import com.demo.expense.model.Expense;
import com.demo.expense.model.ReimbursementResult;
import com.demo.expense.rule.BudgetRule;
import com.demo.expense.rule.Rule;
import com.demo.expense.rule.TripRule;

import java.util.List;
import java.util.Map;

public interface IExpenseService {
    void addExpense(Expense expense);
    void addRule(Rule rule);
    void addTripRule(TripRule tripRule);
    void addBudgetRule(BudgetRule budgetRule);
    List<Expense> evaluateExpenses();
    Map<String, List<Expense>> evaluateTrips();
    Map<String, ReimbursementResult> calculateReimbursement();
}
