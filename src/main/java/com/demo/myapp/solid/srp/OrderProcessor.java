package com.demo.myapp.solid.srp;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// SRP: this class's ONLY responsibility is orchestration — calling the right
// collaborators in the right order. It has no tax logic, no discount logic, and
// no formatting logic of its own, so it never needs to change when any of those
// three concerns change.
//
// Anti-pattern this avoids: a single "OrderService" god-class that computes tax,
// computes discounts, AND formats receipts — three unrelated reasons for one
// class to change, so a tax-law update and a receipt wording tweak both risk
// touching (and breaking) the same file and the same code review.
@Service
public class OrderProcessor {

    private final TaxCalculator taxCalculator;
    private final DiscountEngine discountEngine;
    private final ReceiptFormatter receiptFormatter;

    public OrderProcessor(TaxCalculator taxCalculator, DiscountEngine discountEngine, ReceiptFormatter receiptFormatter) {
        this.taxCalculator = taxCalculator;
        this.discountEngine = discountEngine;
        this.receiptFormatter = receiptFormatter;
    }

    public String processOrder(String orderId, BigDecimal subtotal, boolean loyaltyMember) {
        BigDecimal discount = discountEngine.apply(subtotal, loyaltyMember);
        BigDecimal taxableAmount = subtotal.subtract(discount);
        BigDecimal tax = taxCalculator.calculate(taxableAmount);
        BigDecimal total = taxableAmount.add(tax);
        return receiptFormatter.format(orderId, subtotal, discount, tax, total);
    }
}