package com.demo.expense.service;

import com.demo.expense.model.Expense;
import com.demo.expense.model.ExpenseCategory;
import com.demo.expense.model.ExpenseReimbursement;
import com.demo.expense.model.ReimbursementResult;
import com.demo.expense.rule.BudgetRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReimbursementTest {

    private ExpenseService service;

    @BeforeEach
    void setUp() {
        service = new ExpenseService();
    }

    private Expense expense(String id, String tripId, String amount, ExpenseCategory category) {
        return Expense.builder()
                .id(id).employeeId("emp1").tripId(tripId)
                .amount(new BigDecimal(amount))
                .category(category).merchant("M")
                .date(LocalDate.of(2026, 6, 1))
                .build();
    }

    private ExpenseReimbursement findDetail(ReimbursementResult result, String expenseId) {
        return result.getDetails().stream()
                .filter(d -> d.getExpenseId().equals(expenseId))
                .findFirst().orElseThrow();
    }

    // ── Case 1: fully within budget ───────────────────────────────────────────

    @Test
    void fullyApproved_whenTotalBelowBudget() {
        service.addBudgetRule(new BudgetRule(ExpenseCategory.MEALS, new BigDecimal("200")));
        service.addExpense(expense("e1", "t1", "80.00",  ExpenseCategory.MEALS));
        service.addExpense(expense("e2", "t1", "70.00",  ExpenseCategory.MEALS));  // total = 150 < 200

        ReimbursementResult result = service.calculateReimbursement().get("t1");

        assertThat(findDetail(result, "e1").getApproved()).isEqualByComparingTo("80.00");
        assertThat(findDetail(result, "e1").getRejected()).isEqualByComparingTo("0");
        assertThat(findDetail(result, "e2").getApproved()).isEqualByComparingTo("70.00");
        assertThat(findDetail(result, "e2").getRejected()).isEqualByComparingTo("0");
        assertThat(result.getTotalApproved()).isEqualByComparingTo("150.00");
        assertThat(result.getTotalRejected()).isEqualByComparingTo("0");
    }

    // ── Case 2: overlapping — the core case ───────────────────────────────────

    @Test
    void partialApproval_whenSecondExpenseStradlesBudget() {
        service.addBudgetRule(new BudgetRule(ExpenseCategory.MEALS, new BigDecimal("120")));
        service.addExpense(expense("e1", "t1", "100.00", ExpenseCategory.MEALS));
        service.addExpense(expense("e2", "t1", "50.00",  ExpenseCategory.MEALS));
        // total = 150, budget = 120
        // e1: 100 ≤ 120 → fully approved, remaining = 20
        // e2: 50 > 20  → approved = 20, rejected = 30

        ReimbursementResult result = service.calculateReimbursement().get("t1");

        assertThat(findDetail(result, "e1").getApproved()).isEqualByComparingTo("100.00");
        assertThat(findDetail(result, "e1").getRejected()).isEqualByComparingTo("0");
        assertThat(findDetail(result, "e2").getApproved()).isEqualByComparingTo("20.00");
        assertThat(findDetail(result, "e2").getRejected()).isEqualByComparingTo("30.00");
        assertThat(result.getTotalApproved()).isEqualByComparingTo("120.00");
        assertThat(result.getTotalRejected()).isEqualByComparingTo("30.00");
    }

    // ── Case 3: budget already exhausted ─────────────────────────────────────

    @Test
    void fullyRejected_whenBudgetAlreadyExhausted() {
        service.addBudgetRule(new BudgetRule(ExpenseCategory.MEALS, new BigDecimal("120")));
        service.addExpense(expense("e1", "t1", "120.00", ExpenseCategory.MEALS)); // exhausts budget
        service.addExpense(expense("e2", "t1", "40.00",  ExpenseCategory.MEALS)); // fully rejected
        service.addExpense(expense("e3", "t1", "30.00",  ExpenseCategory.MEALS)); // fully rejected

        ReimbursementResult result = service.calculateReimbursement().get("t1");

        assertThat(findDetail(result, "e1").getApproved()).isEqualByComparingTo("120.00");
        assertThat(findDetail(result, "e2").getApproved()).isEqualByComparingTo("0");
        assertThat(findDetail(result, "e2").getRejected()).isEqualByComparingTo("40.00");
        assertThat(findDetail(result, "e3").getApproved()).isEqualByComparingTo("0");
        assertThat(findDetail(result, "e3").getRejected()).isEqualByComparingTo("30.00");
        assertThat(result.getTotalApproved()).isEqualByComparingTo("120.00");
        assertThat(result.getTotalRejected()).isEqualByComparingTo("70.00");
    }

    // ── No budget rule → fully approved ──────────────────────────────────────

    @Test
    void fullyApproved_whenNoBudgetRuleForCategory() {
        // No budget rule added for TRAVEL
        service.addExpense(expense("e1", "t1", "500.00", ExpenseCategory.TRAVEL));

        ReimbursementResult result = service.calculateReimbursement().get("t1");

        assertThat(findDetail(result, "e1").getApproved()).isEqualByComparingTo("500.00");
        assertThat(findDetail(result, "e1").getRejected()).isEqualByComparingTo("0");
        assertThat(result.getTotalApproved()).isEqualByComparingTo("500.00");
        assertThat(result.getTotalRejected()).isEqualByComparingTo("0");
    }

    // ── Multiple categories, mixed budget rules ───────────────────────────────

    @Test
    void multipleCategories_eachWithOwnBudget() {
        service.addBudgetRule(new BudgetRule(ExpenseCategory.MEALS,  new BigDecimal("120")));
        service.addBudgetRule(new BudgetRule(ExpenseCategory.TRAVEL, new BigDecimal("500")));

        service.addExpense(expense("e1", "t1", "100.00", ExpenseCategory.MEALS));
        service.addExpense(expense("e2", "t1", "50.00",  ExpenseCategory.MEALS));   // $20 approved, $30 rejected
        service.addExpense(expense("e3", "t1", "400.00", ExpenseCategory.TRAVEL));  // fully approved
        service.addExpense(expense("e4", "t1", "200.00", ExpenseCategory.TRAVEL));  // $100 approved, $100 rejected

        ReimbursementResult result = service.calculateReimbursement().get("t1");

        assertThat(result.getTotalApproved()).isEqualByComparingTo("620.00"); // 120 + 500
        assertThat(result.getTotalRejected()).isEqualByComparingTo("130.00"); // 30 + 100
    }

    // ── Multiple trips calculated independently ───────────────────────────────

    @Test
    void multipleTrips_calculatedIndependently() {
        service.addBudgetRule(new BudgetRule(ExpenseCategory.MEALS, new BigDecimal("100")));

        // t1 stays within budget
        service.addExpense(expense("e1", "t1", "80.00", ExpenseCategory.MEALS));
        // t2 exceeds budget
        service.addExpense(expense("e2", "t2", "150.00", ExpenseCategory.MEALS));

        Map<String, ReimbursementResult> results = service.calculateReimbursement();

        assertThat(results.get("t1").getTotalApproved()).isEqualByComparingTo("80.00");
        assertThat(results.get("t1").getTotalRejected()).isEqualByComparingTo("0");
        assertThat(results.get("t2").getTotalApproved()).isEqualByComparingTo("100.00");
        assertThat(results.get("t2").getTotalRejected()).isEqualByComparingTo("50.00");
    }
}
