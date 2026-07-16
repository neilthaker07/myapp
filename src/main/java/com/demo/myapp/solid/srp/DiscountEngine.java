package com.demo.myapp.solid.srp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

// SRP: the only reason this class changes is discount policy (e.g. marketing
// adjusts the loyalty percentage). It knows nothing about tax or receipts.
@Component
public class DiscountEngine {

    private static final BigDecimal LOYALTY_DISCOUNT = new BigDecimal("0.10"); // 10% off for loyalty members

    public BigDecimal apply(BigDecimal subtotal, boolean loyaltyMember) {
        if (!loyaltyMember) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return subtotal.multiply(LOYALTY_DISCOUNT).setScale(2, RoundingMode.HALF_UP);
    }
}