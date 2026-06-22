package com.demo.expense.rule;

import java.math.BigDecimal;

public enum RuleOperator {

    GREATER_THAN {
        @Override
        public boolean evaluate(String fieldValue, String ruleValue) {
            // Part 1 bug: "9".compareTo("153") > 0 → true (wrong — string sorts "9" after "1")
            // Fix: parse to BigDecimal so 9 < 153 correctly
            try {
                return new BigDecimal(fieldValue).compareTo(new BigDecimal(ruleValue)) > 0;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "GREATER_THAN requires numeric values, got: '" + fieldValue + "', '" + ruleValue + "'");
            }
        }
    },
    LESS_THAN {
        @Override
        public boolean evaluate(String fieldValue, String ruleValue) {
            try {
                return new BigDecimal(fieldValue).compareTo(new BigDecimal(ruleValue)) < 0;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "LESS_THAN requires numeric values, got: '" + fieldValue + "', '" + ruleValue + "'");
            }
        }
    },
    EQUALS {
        @Override
        public boolean evaluate(String fieldValue, String ruleValue) {
            // Bug: "500.00".equalsIgnoreCase("500") → false (same amount, different string form)
            // Fix: try numeric comparison first; fall back to string for category/merchant
            try {
                return new BigDecimal(fieldValue).compareTo(new BigDecimal(ruleValue)) == 0;
            } catch (NumberFormatException e) {
                return fieldValue.equalsIgnoreCase(ruleValue);
            }
        }
    },
    CONTAINS {
        @Override
        public boolean evaluate(String fieldValue, String ruleValue) {
            return fieldValue.toLowerCase().contains(ruleValue.toLowerCase());
        }
    };

    public abstract boolean evaluate(String fieldValue, String ruleValue);
}
