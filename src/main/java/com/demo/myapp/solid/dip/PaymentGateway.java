package com.demo.myapp.solid.dip;

import java.math.BigDecimal;

// DIP: CheckoutService (the high-level policy) owns this interface. Low-level
// gateway implementations depend on IT — not the other way around. This
// inversion is what makes CheckoutService unit-testable with a stub gateway,
// and lets the real vendor be swapped (Stripe -> Square) without touching
// checkout logic at all.
public interface PaymentGateway {
    String submitPayment(BigDecimal amount);
}
