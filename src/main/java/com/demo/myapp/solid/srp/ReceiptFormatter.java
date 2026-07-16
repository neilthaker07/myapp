package com.demo.myapp.solid.srp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// SRP: the only reason this class changes is presentation — line wording,
// currency formatting, localization. It never changes when tax or discount
// rules change, even though its output depends on their results.
@Component
public class ReceiptFormatter {

    public String format(String orderId, BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal total) {
        return """
                Order:     %s
                Subtotal:  $%s
                Discount: -$%s
                Tax:      +$%s
                -----------------
                Total:      $%s
                """.formatted(orderId, subtotal, discount, tax, total);
    }
}