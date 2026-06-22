package com.demo.expense.rule;

import com.demo.expense.model.Expense;

public enum RuleField {

    AMOUNT {
        @Override
        public String extract(Expense expense) {
            return expense.getAmount().toPlainString();
        }
    },
    CATEGORY {
        @Override
        public String extract(Expense expense) {
            return expense.getCategory().name();
        }
    },
    MERCHANT {
        @Override
        public String extract(Expense expense) {
            return expense.getMerchant();
        }
    };

    public abstract String extract(Expense expense);
}
