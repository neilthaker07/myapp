package com.demo.expense.dto;

import com.demo.expense.model.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
public class AddExpenseRequest {
    @NotBlank String id;
    @NotBlank String employeeId;
    @NotBlank String tripId;
    @NotNull @DecimalMin("0.01") BigDecimal amount;
    @NotNull ExpenseCategory category;
    @NotBlank String merchant;
    @NotNull LocalDate date;
}
