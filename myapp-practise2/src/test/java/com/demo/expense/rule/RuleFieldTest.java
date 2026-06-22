package com.demo.expense.rule;

import com.demo.expense.model.Expense;
import com.demo.expense.model.ExpenseCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RuleFieldTest {

    private static final Expense EXPENSE = Expense.builder()
            .id("e1")
            .employeeId("emp1")
            .tripId("t1")
            .amount(new BigDecimal("153.00"))
            .category(ExpenseCategory.TRAVEL)
            .merchant("Airbnb San Francisco")
            .date(LocalDate.of(2026, 6, 1))
            .build();

    @Test
    void amount_extractsAsPlainString() {
        // toPlainString() avoids scientific notation like "1.53E+2"
        assertThat(RuleField.AMOUNT.extract(EXPENSE)).isEqualTo("153.00");
    }

    @Test
    void category_extractsEnumName() {
        assertThat(RuleField.CATEGORY.extract(EXPENSE)).isEqualTo("TRAVEL");
    }

    @Test
    void merchant_extractsAsIs() {
        assertThat(RuleField.MERCHANT.extract(EXPENSE)).isEqualTo("Airbnb San Francisco");
    }
}
