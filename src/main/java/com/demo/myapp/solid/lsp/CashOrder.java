package com.demo.myapp.solid.lsp;

import java.math.BigDecimal;

public class CashOrder extends Order {

    public CashOrder(String orderId, BigDecimal amount) {
        super(orderId, amount);
    }

    @Override
    public String refund() {
        return "Returned $" + amount + " cash from the register for order " + orderId;
    }
}