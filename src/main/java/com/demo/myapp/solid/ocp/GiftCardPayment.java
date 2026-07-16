package com.demo.myapp.solid.ocp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("giftcard")
public class GiftCardPayment implements PaymentMethod {

    @Override
    public String charge(BigDecimal amount) {
        return "Deducted $" + amount + " from gift card balance";
    }
}