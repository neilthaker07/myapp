package com.demo.expense.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class Expense {
    String id;
    String employeeId;
    String tripId;
    BigDecimal amount;
    ExpenseCategory category;
    String merchant;
    LocalDate date;
}
