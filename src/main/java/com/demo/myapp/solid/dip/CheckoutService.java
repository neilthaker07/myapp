package com.demo.myapp.solid.dip;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// DIP: CheckoutService is the HIGH-LEVEL module — it depends only on the
// PaymentGateway abstraction, injected by Spring. It has zero knowledge of the
// Stripe or Square SDKs. Swap the concrete bean passed in (or inject a mock in
// a test) and this class's source never changes.
@Service
public class CheckoutService {

    private final PaymentGateway paymentGateway;

    public CheckoutService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String checkout(BigDecimal amount) {
        return paymentGateway.submitPayment(amount);
    }
}
