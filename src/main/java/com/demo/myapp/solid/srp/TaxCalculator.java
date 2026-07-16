package com.demo.myapp.solid.srp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

// SRP: this class has exactly one reason to change — the tax rate/rules.
// Discounting and receipt formatting live in their own classes below, so a
// tax-law update never risks touching either of them.
@Component
public class TaxCalculator {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.0825"); // sample POS sales tax

    public BigDecimal calculate(BigDecimal taxableAmount) {
        return taxableAmount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}