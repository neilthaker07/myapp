package com.demo.expense.rule;

import com.demo.expense.model.Expense;
import com.demo.expense.model.ExpenseCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseTripRuleTest {

    private Expense buildExpense(String id, String amount) {
        return Expense.builder()
                .id(id).employeeId("emp1").tripId("t1")
                .amount(new BigDecimal(amount))
                .category(ExpenseCategory.TRAVEL)
                .merchant("Marriott")
                .date(LocalDate.of(2026, 6, 1))
                .build();
    }

    // ── TOTAL_AMOUNT ──────────────────────────────────────────────────────────

    @Test
    void totalAmount_isViolated_whenTripExceedsThreshold() {
        ExpenseTripRule rule = new ExpenseTripRule(TripRuleField.TOTAL_AMOUNT, RuleOperator.GREATER_THAN, "1000");
        List<Expense> trip = List.of(
                buildExpense("e1", "600.00"),
                buildExpense("e2", "500.00")   // total = 1100 → exceeds 1000
        );
        assertThat(rule.isViolated(trip)).isTrue();
    }

    @Test
    void totalAmount_notViolated_whenTripBelowThreshold() {
        ExpenseTripRule rule = new ExpenseTripRule(TripRuleField.TOTAL_AMOUNT, RuleOperator.GREATER_THAN, "1000");
        List<Expense> trip = List.of(
                buildExpense("e1", "400.00"),
                buildExpense("e2", "300.00")   // total = 700 → under 1000
        );
        assertThat(rule.isViolated(trip)).isFalse();
    }

    @Test
    void totalAmount_exactlyAtThreshold_notViolated() {
        ExpenseTripRule rule = new ExpenseTripRule(TripRuleField.TOTAL_AMOUNT, RuleOperator.GREATER_THAN, "1000");
        List<Expense> trip = List.of(
                buildExpense("e1", "500.00"),
                buildExpense("e2", "500.00")   // total = 1000 → not > 1000
        );
        assertThat(rule.isViolated(trip)).isFalse();
    }

    // ── EXPENSE_COUNT ─────────────────────────────────────────────────────────

    @Test
    void expenseCount_isViolated_whenTooManyExpenses() {
        ExpenseTripRule rule = new ExpenseTripRule(TripRuleField.EXPENSE_COUNT, RuleOperator.GREATER_THAN, "3");
        List<Expense> trip = List.of(
                buildExpense("e1", "100"), buildExpense("e2", "100"),
                buildExpense("e3", "100"), buildExpense("e4", "100")  // 4 > 3
        );
        assertThat(rule.isViolated(trip)).isTrue();
    }

    @Test
    void expenseCount_notViolated_whenWithinLimit() {
        ExpenseTripRule rule = new ExpenseTripRule(TripRuleField.EXPENSE_COUNT, RuleOperator.GREATER_THAN, "3");
        List<Expense> trip = List.of(
                buildExpense("e1", "100"), buildExpense("e2", "100")  // 2 ≤ 3
        );
        assertThat(rule.isViolated(trip)).isFalse();
    }

    // ── describe() ────────────────────────────────────────────────────────────

    @Test
    void describe_returnsHumanReadableString() {
        ExpenseTripRule rule = new ExpenseTripRule(TripRuleField.TOTAL_AMOUNT, RuleOperator.GREATER_THAN, "1000");
        assertThat(rule.describe()).isEqualTo("TOTAL_AMOUNT GREATER_THAN 1000");
    }
}
