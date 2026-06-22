package com.demo.expense.rule;

import com.demo.expense.model.Expense;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AndRule implements Rule {

    private final List<Rule> rules;

    @Override
    public boolean isViolated(Expense expense) {
        return rules.stream().allMatch(rule -> rule.isViolated(expense));
    }

    @Override
    public String describe() {
        return "AND(" + rules.stream().map(Rule::describe).collect(Collectors.joining(", ")) + ")";
    }
}
