package com.demo.expense.rule;

import com.demo.expense.model.ExpenseCategory;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class BudgetRule {
    ExpenseCategory category;
    BigDecimal limit;
}
