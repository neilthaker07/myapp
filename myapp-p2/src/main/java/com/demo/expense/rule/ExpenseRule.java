package com.demo.expense.rule;

import com.demo.expense.model.Expense;
import lombok.Value;

@Value
public class ExpenseRule implements Rule {

    RuleField field;
    RuleOperator operator;
    String value;

    @Override
    public boolean isViolated(Expense expense) {
        String fieldValue = field.extract(expense);
        return operator.evaluate(fieldValue, value);
    }

    @Override
    public String describe() {
        return field + " " + operator + " " + value;
    }
}
