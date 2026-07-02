package com.demo.rippling.rule;

import com.demo.rippling.model.Expense;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Optional;

// "No restaurant expense can exceed $75"
// e.g. VendorTypeMaxAmountRule("restaurant", 75)
//   + expense(vendor="restaurant", $49.99) → empty (passes)
//   + expense(vendor="restaurant", $100)   → "restaurant vendor expenses cannot exceed $75"
//   + expense(vendor="hotel",      $100)   → empty (different vendor type, skip)
@Value
public class VendorTypeMaxAmountRule implements Rule {

    String vendorType;
    BigDecimal maxAmount;

    @Override
    public Optional<String> evaluate(Expense expense) {
        // Skip — this rule only applies to the configured vendor type
        if (!expense.getVendorType().equalsIgnoreCase(vendorType)) return Optional.empty();

        return expense.getAmount().compareTo(maxAmount) > 0
                ? Optional.of(String.format(
                        "'%s' vendor expenses cannot exceed $%s (submitted: $%s)",
                        vendorType, maxAmount.toPlainString(), expense.getAmount().toPlainString()))
                : Optional.empty();
    }
}
