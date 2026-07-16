package com.demo.myapp.solid.dip;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// DIP in action: an entirely different low-level payment vendor, plugged in
// without CheckoutService — the high-level module — changing by a single line.
@Component
public class SquareGatewayImpl implements PaymentGateway {

    @Override
    public String submitPayment(BigDecimal amount) {
        return "Square: submitted $" + amount;
    }
}
