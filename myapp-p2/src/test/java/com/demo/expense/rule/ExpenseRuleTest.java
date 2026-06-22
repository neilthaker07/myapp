package com.demo.expense.rule;

import com.demo.expense.model.Expense;
import com.demo.expense.model.ExpenseCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseRuleTest {

    private Expense buildExpense(String amount, ExpenseCategory category, String merchant) {
        return Expense.builder()
                .id("e1")
                .employeeId("emp1")
                .tripId("t1")
                .amount(new BigDecimal(amount))
                .category(category)
                .merchant(merchant)
                .date(LocalDate.of(2026, 6, 1))
                .build();
    }

    // ── Amount rules ──────────────────────────────────────────────────────────

    @Test
    void amountGreaterThan_isViolated_whenExpenseExceedsThreshold() {
        ExpenseRule rule = new ExpenseRule(RuleField.AMOUNT, RuleOperator.GREATER_THAN, "500");
        assertThat(rule.isViolated(buildExpense("600.00", ExpenseCategory.TRAVEL, "Marriott"))).isTrue();
    }

    @Test
    void amountGreaterThan_notViolated_whenExpenseBelowThreshold() {
        ExpenseRule rule = new ExpenseRule(RuleField.AMOUNT, RuleOperator.GREATER_THAN, "500");
        assertThat(rule.isViolated(buildExpense("400.00", ExpenseCategory.TRAVEL, "Marriott"))).isFalse();
    }

    @Test
    void amountEquals_isViolated_withDifferentStringForms() {
        ExpenseRule rule = new ExpenseRule(RuleField.AMOUNT, RuleOperator.EQUALS, "500");
        assertThat(rule.isViolated(buildExpense("500.00", ExpenseCategory.MEALS, "Chipotle"))).isTrue();
        assertThat(rule.isViolated(buildExpense("500.0",  ExpenseCategory.MEALS, "Chipotle"))).isTrue();
    }

    // ── Category rules ────────────────────────────────────────────────────────

    @Test
    void categoryEquals_isViolated_whenCategoryMatches() {
        ExpenseRule rule = new ExpenseRule(RuleField.CATEGORY, RuleOperator.EQUALS, "TRAVEL");
        assertThat(rule.isViolated(buildExpense("200.00", ExpenseCategory.TRAVEL, "Delta"))).isTrue();
    }

    @Test
    void categoryEquals_notViolated_whenCategoryDiffers() {
        ExpenseRule rule = new ExpenseRule(RuleField.CATEGORY, RuleOperator.EQUALS, "TRAVEL");
        assertThat(rule.isViolated(buildExpense("200.00", ExpenseCategory.MEALS, "Chipotle"))).isFalse();
    }

    @Test
    void categoryEquals_isCaseInsensitive() {
        ExpenseRule rule = new ExpenseRule(RuleField.CATEGORY, RuleOperator.EQUALS, "travel");
        assertThat(rule.isViolated(buildExpense("200.00", ExpenseCategory.TRAVEL, "Delta"))).isTrue();
    }

    // ── Merchant rules ────────────────────────────────────────────────────────

    @Test
    void merchantContains_isViolated_whenKeywordPresent() {
        ExpenseRule rule = new ExpenseRule(RuleField.MERCHANT, RuleOperator.CONTAINS, "Airbnb");
        assertThat(rule.isViolated(buildExpense("300.00", ExpenseCategory.TRAVEL, "Airbnb San Francisco"))).isTrue();
    }

    @Test
    void merchantContains_notViolated_whenKeywordAbsent() {
        ExpenseRule rule = new ExpenseRule(RuleField.MERCHANT, RuleOperator.CONTAINS, "Airbnb");
        assertThat(rule.isViolated(buildExpense("300.00", ExpenseCategory.TRAVEL, "Marriott"))).isFalse();
    }

    // ── describe() ────────────────────────────────────────────────────────────

    @Test
    void describe_returnsHumanReadableString() {
        ExpenseRule rule = new ExpenseRule(RuleField.AMOUNT, RuleOperator.GREATER_THAN, "500");
        assertThat(rule.describe()).isEqualTo("AMOUNT GREATER_THAN 500");
    }
}
