package com.demo.rippling.rule;

/**
 * Factory for a per-trip stateful evaluator.
 * newEvaluator() is called once per trip — each trip gets its own fresh running total.
 */
public interface TripRule {
    TripRuleEvaluator newEvaluator();
}
