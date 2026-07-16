package com.demo.myapp.solid.ocp;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

// OCP: depends only on the PaymentMethod abstraction. Spring injects every bean
// implementing PaymentMethod, keyed by its @Component qualifier name — this
// class's source hasn't changed since TapToPayPayment was added, and won't
// change for the next tender type either. A new payment method is purely an
// addition, never an edit to this file's if/switch logic.
@Service
public class PaymentProcessor {

    private final Map<String, PaymentMethod> paymentMethodsByTenderType;

    public PaymentProcessor(Map<String, PaymentMethod> paymentMethodsByTenderType) {
        this.paymentMethodsByTenderType = paymentMethodsByTenderType;
    }

    public String process(String tenderType, BigDecimal amount) {
        PaymentMethod method = paymentMethodsByTenderType.get(tenderType.toLowerCase());
        if (method == null) {
            throw new IllegalArgumentException("Unsupported tender type: " + tenderType);
        }
        return method.charge(amount);
    }
}