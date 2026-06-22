package com.demo.expense.rule;

import com.demo.expense.model.Expense;
import lombok.Value;

import java.util.List;

@Value
public class ExpenseTripRule implements TripRule {

    TripRuleField field;
    RuleOperator operator;  // reuse — same comparison logic as per-expense rules
    String value;

    @Override
    public boolean isViolated(List<Expense> tripExpenses) {
        String fieldValue = field.extract(tripExpenses);
        return operator.evaluate(fieldValue, value);
    }

    @Override
    public String describe() {
        return field + " " + operator + " " + value;
    }
}
