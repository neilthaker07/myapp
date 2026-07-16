package com.demo.myapp.solid.ocp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// OCP in action: this class was added later to support a new tender type.
// Nothing in PaymentMethod or PaymentProcessor changed to accommodate it —
// extension without modification.
@Component("taptopay")
public class TapToPayPayment implements PaymentMethod {

    @Override
    public String charge(BigDecimal amount) {
        return "Charged $" + amount + " via NFC tap-to-pay";
    }
}