package com.demo.myapp.solid.isp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// ISP: a card gateway genuinely supports all three capabilities, so it
// implements all three narrow interfaces — no forced no-op methods, because
// every method it implements it actually needs.
@Component
public class CardGateway implements Chargeable, Refundable, Tokenizable {

    @Override
    public String charge(BigDecimal amount) {
        return "Card gateway charged $" + amount;
    }

    @Override
    public String refund(BigDecimal amount) {
        return "Card gateway refunded $" + amount;
    }

    @Override
    public String tokenize(String rawCardNumber) {
        return "tok_" + Integer.toHexString(rawCardNumber.hashCode());
    }
}
