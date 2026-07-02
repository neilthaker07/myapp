package com.demo.rippling.model;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Map;

@Value
public class Expense {

    String expenseId;
    String tripId;
    BigDecimal amount;
    String expenseType;  // "airfare", "entertainment", "client_hosting", ...
    String vendorType;   // "restaurant", "hotel", ...
    String vendorName;

    // Entry point for raw map data from the API / caller
    // Guards the boundary: all internal code works with typed Expense, never raw maps
    public static Expense fromMap(Map<String, String> map) {
        return new Expense(
                map.get("expense_id"),
                map.get("trip_id"),
                new BigDecimal(map.getOrDefault("amount_usd", "0")),
                map.getOrDefault("expense_type", ""),
                map.getOrDefault("vendor_type", ""),
                map.getOrDefault("vendor_name", "")
        );
    }
}
