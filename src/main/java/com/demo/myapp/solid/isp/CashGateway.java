package com.demo.myapp.solid.isp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// ISP in action: physical cash can be charged and refunded but can never be
// tokenized — there's no card number to represent as a token. Because
// Tokenizable is its own narrow interface, CashGateway just doesn't implement
// it, instead of implementing one fat PaymentGateway interface and throwing
// UnsupportedOperationException from a forced tokenize() method.
@Component
public class CashGateway implements Chargeable, Refundable {

    @Override
    public String charge(BigDecimal amount) {
        return "Cash drawer charged $" + amount;
    }

    @Override
    public String refund(BigDecimal amount) {
        return "Cash drawer refunded $" + amount;
    }
}
