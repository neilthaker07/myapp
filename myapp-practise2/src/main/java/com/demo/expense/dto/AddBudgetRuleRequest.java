package com.demo.expense.dto;

import com.demo.expense.model.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class AddBudgetRuleRequest {
    @NotNull ExpenseCategory category;
    @NotNull @DecimalMin("0.01") BigDecimal limit;
}
