package com.demo.expense.dto;

import com.demo.expense.rule.RuleField;
import com.demo.expense.rule.RuleOperator;
import lombok.Data;

import java.util.List;

// Recursive DTO — one class represents all rule types:
//
// SIMPLE: { "type": "SIMPLE", "field": "AMOUNT", "operator": "GREATER_THAN", "value": "100" }
// AND:    { "type": "AND",    "rules": [ <CompositeRuleRequest>, ... ] }
// OR:     { "type": "OR",     "rules": [ <CompositeRuleRequest>, ... ] }
// NOT:    { "type": "NOT",    "rule":  <CompositeRuleRequest> }
@Data
public class CompositeRuleRequest {
    private String type;           // SIMPLE | AND | OR | NOT

    // SIMPLE fields
    private RuleField field;
    private RuleOperator operator;
    private String value;

    // AND / OR fields
    private List<CompositeRuleRequest> rules;

    // NOT field
    private CompositeRuleRequest rule;
}
