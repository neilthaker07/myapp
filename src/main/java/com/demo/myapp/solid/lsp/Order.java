package com.demo.myapp.solid.lsp;

import java.math.BigDecimal;

// LSP: every subtype must honor this contract — refund() always succeeds and
// returns a confirmation message, never throws for a "kind" of order the caller
// didn't special-case. Client code (OrderRefundService) can call refund() on
// ANY Order without an instanceof check.
public abstract class Order {

    protected final String orderId;
    protected final BigDecimal amount;

    protected Order(String orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public abstract String refund();

    public String getOrderId() {
        return orderId;
    }
}