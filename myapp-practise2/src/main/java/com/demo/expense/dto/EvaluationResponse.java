package com.demo.expense.dto;

import com.demo.expense.model.Expense;
import lombok.Value;

import java.util.List;

@Value
public class EvaluationResponse {
    int violationCount;
    List<Expense> violations;
}
