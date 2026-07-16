package com.demo.myapp.solid.lsp;

import java.math.BigDecimal;

// LSP done right: a gift-card order can't refund to a card network, but instead
// of throwing UnsupportedOperationException (which would break every caller
// that polymorphically invokes refund()), it substitutes an equally valid
// fulfillment of the SAME contract — store credit. Callers never need to know
// the difference between this and CardOrder/CashOrder.
public class GiftCardOrder extends Order {

    public GiftCardOrder(String orderId, BigDecimal amount) {
        super(orderId, amount);
    }

    @Override
    public String refund() {
        return "Issued $" + amount + " store credit for order " + orderId;
    }
}
