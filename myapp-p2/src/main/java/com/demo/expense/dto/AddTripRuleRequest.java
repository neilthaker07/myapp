package com.demo.expense.dto;

import com.demo.expense.rule.RuleOperator;
import com.demo.expense.rule.TripRuleField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class AddTripRuleRequest {
    @NotNull TripRuleField field;
    @NotNull RuleOperator operator;
    @NotBlank String value;
}
