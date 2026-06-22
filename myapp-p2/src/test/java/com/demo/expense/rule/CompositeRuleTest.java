package com.demo.expense.rule;

import com.demo.expense.model.Expense;
import com.demo.expense.model.ExpenseCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompositeRuleTest {

    private Expense buildExpense(String amount, ExpenseCategory category, String merchant) {
        return Expense.builder()
                .id("e1").employeeId("emp1").tripId("t1")
                .amount(new BigDecimal(amount))
                .category(category)
                .merchant(merchant)
                .date(LocalDate.of(2026, 6, 1))
                .build();
    }

    private ExpenseRule amountGt(String threshold) {
        return new ExpenseRule(RuleField.AMOUNT, RuleOperator.GREATER_THAN, threshold);
    }

    private ExpenseRule categoryEq(String category) {
        return new ExpenseRule(RuleField.CATEGORY, RuleOperator.EQUALS, category);
    }

    private ExpenseRule merchantContains(String keyword) {
        return new ExpenseRule(RuleField.MERCHANT, RuleOperator.CONTAINS, keyword);
    }

    // ── AndRule ───────────────────────────────────────────────────────────────

    @Test
    void and_isViolated_whenAllRulesViolated() {
        AndRule rule = new AndRule(List.of(amountGt("100"), categoryEq("TRAVEL")));
        Expense expense = buildExpense("200.00", ExpenseCategory.TRAVEL, "Delta");
        assertThat(rule.isViolated(expense)).isTrue();
    }

    @Test
    void and_notViolated_whenOnlyOneRuleViolated() {
        AndRule rule = new AndRule(List.of(amountGt("100"), categoryEq("TRAVEL")));
        // amount ok (50 < 100), category matches — only one rule fires
        Expense expense = buildExpense("50.00", ExpenseCategory.TRAVEL, "Delta");
        assertThat(rule.isViolated(expense)).isFalse();
    }

    @Test
    void and_notViolated_whenNoRulesViolated() {
        AndRule rule = new AndRule(List.of(amountGt("100"), categoryEq("TRAVEL")));
        Expense expense = buildExpense("50.00", ExpenseCategory.MEALS, "Chipotle");
        assertThat(rule.isViolated(expense)).isFalse();
    }

    // ── OrRule ────────────────────────────────────────────────────────────────

    @Test
    void or_isViolated_whenAtLeastOneRuleViolated() {
        OrRule rule = new OrRule(List.of(amountGt("500"), categoryEq("MEALS")));
        // only amount rule fires
        Expense expense = buildExpense("600.00", ExpenseCategory.TRAVEL, "Marriott");
        assertThat(rule.isViolated(expense)).isTrue();
    }

    @Test
    void or_isViolated_whenBothRulesViolated() {
        OrRule rule = new OrRule(List.of(amountGt("500"), categoryEq("MEALS")));
        Expense expense = buildExpense("600.00", ExpenseCategory.MEALS, "Chipotle");
        assertThat(rule.isViolated(expense)).isTrue();
    }

    @Test
    void or_notViolated_whenNoRulesViolated() {
        OrRule rule = new OrRule(List.of(amountGt("500"), categoryEq("MEALS")));
        Expense expense = buildExpense("200.00", ExpenseCategory.TRAVEL, "Delta");
        assertThat(rule.isViolated(expense)).isFalse();
    }

    // ── NotRule ───────────────────────────────────────────────────────────────

    @Test
    void not_isViolated_whenInnerRuleNotViolated() {
        // NOT(category == MEALS) → flag everything that is NOT meals
        NotRule rule = new NotRule(categoryEq("MEALS"));
        Expense expense = buildExpense("100.00", ExpenseCategory.TRAVEL, "Delta");
        assertThat(rule.isViolated(expense)).isTrue();
    }

    @Test
    void not_notViolated_whenInnerRuleViolated() {
        NotRule rule = new NotRule(categoryEq("MEALS"));
        Expense expense = buildExpense("100.00", ExpenseCategory.MEALS, "Chipotle");
        assertThat(rule.isViolated(expense)).isFalse();
    }

    // ── Nested composition ────────────────────────────────────────────────────

    @Test
    void nested_and_with_or() {
        // AND(amount > 100, OR(category == TRAVEL, merchant CONTAINS "Airbnb"))
        Rule rule = new AndRule(List.of(
                amountGt("100"),
                new OrRule(List.of(categoryEq("TRAVEL"), merchantContains("Airbnb")))
        ));

        // violates: amount > 100 AND category == TRAVEL
        assertThat(rule.isViolated(buildExpense("200.00", ExpenseCategory.TRAVEL, "Delta"))).isTrue();
        // violates: amount > 100 AND merchant contains Airbnb
        assertThat(rule.isViolated(buildExpense("200.00", ExpenseCategory.MEALS, "Airbnb NYC"))).isTrue();
        // not violated: amount ok (50 ≤ 100)
        assertThat(rule.isViolated(buildExpense("50.00", ExpenseCategory.TRAVEL, "Delta"))).isFalse();
        // not violated: OR fails (not travel, no Airbnb)
        assertThat(rule.isViolated(buildExpense("200.00", ExpenseCategory.MEALS, "Chipotle"))).isFalse();
    }

    @Test
    void nested_not_and() {
        // NOT(AND(amount > 100, category == TRAVEL))
        // → flag unless BOTH conditions are true together
        Rule rule = new NotRule(new AndRule(List.of(amountGt("100"), categoryEq("TRAVEL"))));

        // both true → AND violated → NOT flips → not violated
        assertThat(rule.isViolated(buildExpense("200.00", ExpenseCategory.TRAVEL, "Delta"))).isFalse();
        // only amount → AND not violated → NOT flips → violated
        assertThat(rule.isViolated(buildExpense("200.00", ExpenseCategory.MEALS, "Chipotle"))).isTrue();
    }

    // ── describe() ────────────────────────────────────────────────────────────

    @Test
    void describe_formatsCorrectly() {
        Rule rule = new AndRule(List.of(amountGt("100"), new NotRule(categoryEq("MEALS"))));
        assertThat(rule.describe()).isEqualTo("AND(AMOUNT GREATER_THAN 100, NOT(CATEGORY EQUALS MEALS))");
    }
}
