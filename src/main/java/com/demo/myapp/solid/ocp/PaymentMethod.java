package com.demo.myapp.solid.ocp;

import java.math.BigDecimal;

// OCP: the abstraction new tender types extend. PaymentProcessor is CLOSED
// against this interface — it never needs modification to support a new
// payment method, only a new implementation.
@FunctionalInterface
public interface PaymentMethod {
    String charge(BigDecimal amount);
}