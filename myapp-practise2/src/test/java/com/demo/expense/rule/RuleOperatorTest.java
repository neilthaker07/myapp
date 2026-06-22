package com.demo.expense.rule;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuleOperatorTest {

    // ── GREATER_THAN ──────────────────────────────────────────────────────────

    @Test
    void greaterThan_returnsTrue_whenFieldValueIsLarger() {
        assertThat(RuleOperator.GREATER_THAN.evaluate("600", "500")).isTrue();
    }

    @Test
    void greaterThan_returnsFalse_whenFieldValueIsSmaller() {
        assertThat(RuleOperator.GREATER_THAN.evaluate("400", "500")).isFalse();
    }

    @Test
    void greaterThan_returnsFalse_whenEqual() {
        assertThat(RuleOperator.GREATER_THAN.evaluate("500", "500")).isFalse();
    }

    // Part 1 bug: string comparison "9" > "153" → true because "9" > "1" alphabetically
    // BigDecimal comparison: 9 < 153 → correctly false
    @Test
    void greaterThan_bugFix_singleDigitVsThreeDigit() {
        assertThat(RuleOperator.GREATER_THAN.evaluate("9", "153")).isFalse();
    }

    @Test
    void greaterThan_handlesDecimalAmounts() {
        assertThat(RuleOperator.GREATER_THAN.evaluate("49.99", "50.00")).isFalse();
        assertThat(RuleOperator.GREATER_THAN.evaluate("50.01", "50.00")).isTrue();
    }

    // ── LESS_THAN ─────────────────────────────────────────────────────────────

    @Test
    void lessThan_returnsTrue_whenFieldValueIsSmaller() {
        assertThat(RuleOperator.LESS_THAN.evaluate("400", "500")).isTrue();
    }

    @Test
    void lessThan_returnsFalse_whenFieldValueIsLarger() {
        assertThat(RuleOperator.LESS_THAN.evaluate("600", "500")).isFalse();
    }

    @Test
    void lessThan_returnsFalse_whenEqual() {
        assertThat(RuleOperator.LESS_THAN.evaluate("500", "500")).isFalse();
    }

    // ── EQUALS ────────────────────────────────────────────────────────────────

    // Bug: "500.00".equalsIgnoreCase("500") → false — different string forms, same amount
    // Fix: BigDecimal comparison treats these as equal
    @Test
    void equals_bugFix_sameAmountDifferentStringForm() {
        assertThat(RuleOperator.EQUALS.evaluate("500.00", "500")).isTrue();
        assertThat(RuleOperator.EQUALS.evaluate("500.0",  "500")).isTrue();
        assertThat(RuleOperator.EQUALS.evaluate("500",    "500.00")).isTrue();
    }

    @Test
    void equals_returnsFalse_forDifferentAmounts() {
        assertThat(RuleOperator.EQUALS.evaluate("400.00", "500.00")).isFalse();
    }

    @Test
    void equals_isCaseInsensitive_forStringFields() {
        assertThat(RuleOperator.EQUALS.evaluate("TRAVEL", "travel")).isTrue();
        assertThat(RuleOperator.EQUALS.evaluate("travel", "TRAVEL")).isTrue();
    }

    @Test
    void equals_returnsFalse_forDifferentCategories() {
        assertThat(RuleOperator.EQUALS.evaluate("TRAVEL", "MEALS")).isFalse();
    }

    // ── CONTAINS ──────────────────────────────────────────────────────────────

    @Test
    void contains_returnsTrue_whenMerchantContainsKeyword() {
        assertThat(RuleOperator.CONTAINS.evaluate("Airbnb San Francisco", "Airbnb")).isTrue();
    }

    @Test
    void contains_isCaseInsensitive() {
        assertThat(RuleOperator.CONTAINS.evaluate("UBER EATS", "uber")).isTrue();
        assertThat(RuleOperator.CONTAINS.evaluate("uber eats", "UBER")).isTrue();
    }

    @Test
    void contains_returnsFalse_whenMerchantDoesNotContainKeyword() {
        assertThat(RuleOperator.CONTAINS.evaluate("Marriott", "Airbnb")).isFalse();
    }
}
