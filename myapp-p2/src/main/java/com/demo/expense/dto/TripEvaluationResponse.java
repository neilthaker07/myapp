package com.demo.expense.dto;

import com.demo.expense.model.Expense;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class TripEvaluationResponse {
    int violatingTripCount;
    Map<String, List<Expense>> violations;  // tripId → expenses in that trip
}
