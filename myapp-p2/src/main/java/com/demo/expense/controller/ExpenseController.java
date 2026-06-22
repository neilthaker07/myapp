package com.demo.expense.controller;

import com.demo.expense.dto.AddBudgetRuleRequest;
import com.demo.expense.dto.AddExpenseRequest;
import com.demo.expense.dto.AddRuleRequest;
import com.demo.expense.dto.AddTripRuleRequest;
import com.demo.expense.dto.CompositeRuleRequest;
import com.demo.expense.dto.EvaluationResponse;
import com.demo.expense.dto.TripEvaluationResponse;
import com.demo.expense.model.ReimbursementResult;
import com.demo.expense.facade.ExpenseFacade;
import com.demo.expense.model.Expense;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseFacade expenseFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addExpense(@Valid @RequestBody AddExpenseRequest request) {
        Expense expense = Expense.builder()
                .id(request.getId())
                .employeeId(request.getEmployeeId())
                .tripId(request.getTripId())
                .amount(request.getAmount())
                .category(request.getCategory())
                .merchant(request.getMerchant())
                .date(request.getDate())
                .build();
        expenseFacade.addExpense(expense);
    }

    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRule(@Valid @RequestBody AddRuleRequest request) {
        expenseFacade.addRule(request.getField(), request.getOperator(), request.getValue());
    }

    @PostMapping("/trip-rules")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTripRule(@Valid @RequestBody AddTripRuleRequest request) {
        expenseFacade.addTripRule(request.getField(), request.getOperator(), request.getValue());
    }

    @PostMapping("/rules/composite")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCompositeRule(@RequestBody CompositeRuleRequest request) {
        expenseFacade.addCompositeRule(request);
    }

    @GetMapping("/evaluate")
    public EvaluationResponse evaluate() {
        List<Expense> violations = expenseFacade.evaluateExpenses();
        return new EvaluationResponse(violations.size(), violations);
    }

    @GetMapping("/evaluate/trips")
    public TripEvaluationResponse evaluateTrips() {
        Map<String, List<Expense>> violations = expenseFacade.evaluateTrips();
        return new TripEvaluationResponse(violations.size(), violations);
    }

    @PostMapping("/budget-rules")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBudgetRule(@Valid @RequestBody AddBudgetRuleRequest request) {
        expenseFacade.addBudgetRule(request.getCategory(), request.getLimit());
    }

    @GetMapping("/reimbursement")
    public Map<String, ReimbursementResult> calculateReimbursement() {
        return expenseFacade.calculateReimbursement();
    }
}
