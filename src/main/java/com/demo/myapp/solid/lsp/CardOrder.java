package com.demo.myapp.solid.lsp;

import java.math.BigDecimal;

public class CardOrder extends Order {

    public CardOrder(String orderId, BigDecimal amount) {
        super(orderId, amount);
    }

    @Override
    public String refund() {
        return "Refunded $" + amount + " to original card for order " + orderId;
    }
}