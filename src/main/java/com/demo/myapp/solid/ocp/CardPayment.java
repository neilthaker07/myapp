package com.demo.myapp.solid.ocp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("card")
public class CardPayment implements PaymentMethod {

    @Override
    public String charge(BigDecimal amount) {
        return "Charged $" + amount + " to card (auth + capture)";
    }
}