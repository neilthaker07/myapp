package com.demo.expense.model;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class ReimbursementResult {
    String tripId;
    BigDecimal totalApproved;
    BigDecimal totalRejected;
    List<ExpenseReimbursement> details;
}
