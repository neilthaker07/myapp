package com.demo.rippling.service;

import com.demo.rippling.model.EvaluatedExpense;
import com.demo.rippling.model.Expense;
import com.demo.rippling.rule.BlockExpenseTypeRule;
import com.demo.rippling.rule.MaxAmountRule;
import com.demo.rippling.rule.Rule;
import com.demo.rippling.rule.VendorTypeMaxAmountRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class ExpenseService {

    private final RulesEngine rulesEngine;

    // CopyOnWriteArrayList: rule reads vastly outnumber writes.
    // Reads are lock-free; writes (addRule) copy the internal array — safe under concurrent access.
    private final List<Rule> rules = new CopyOnWriteArrayList<>();

    public ExpenseService(RulesEngine rulesEngine) {
        this.rulesEngine = rulesEngine;
        initDefaultRules();
    }

    private void initDefaultRules() {
        rules.add(new BlockExpenseTypeRule("airfare"));
        rules.add(new BlockExpenseTypeRule("entertainment"));
        rules.add(new VendorTypeMaxAmountRule("restaurant", new BigDecimal("75")));
        rules.add(new MaxAmountRule(new BigDecimal("250")));
    }

    public List<EvaluatedExpense> evaluate(List<Map<String, String>> rawExpenses) {
        List<Expense> expenses = rawExpenses.stream()
                .map(Expense::fromMap)
                .toList();
        return rulesEngine.evaluateRules(rules, expenses);
    }

    // Hook for Part 2+: API-driven rule creation will call this
    public void addRule(Rule rule) {
        rules.add(rule);
        log.info("Rule added. Total active rules: {}", rules.size());
    }
}
