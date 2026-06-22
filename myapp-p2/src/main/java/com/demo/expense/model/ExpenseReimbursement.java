package com.demo.expense.model;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class ExpenseReimbursement {
    String expenseId;
    BigDecimal originalAmount;
    BigDecimal approved;
    BigDecimal rejected;
}
