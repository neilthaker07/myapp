package com.demo.myapp.solid.dip;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// DIP: a low-level detail implementing the high-level abstraction.
// @Primary makes this the default PaymentGateway Spring injects wherever no
// other qualifier is requested.
@Primary
@Component
public class StripeGatewayImpl implements PaymentGateway {

    @Override
    public String submitPayment(BigDecimal amount) {
        return "Stripe: submitted $" + amount;
    }
}
