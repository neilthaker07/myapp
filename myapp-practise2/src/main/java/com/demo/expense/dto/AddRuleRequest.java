package com.demo.expense.dto;

import com.demo.expense.rule.RuleField;
import com.demo.expense.rule.RuleOperator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class AddRuleRequest {
    @NotNull RuleField field;
    @NotNull RuleOperator operator;
    @NotBlank String value;
}
