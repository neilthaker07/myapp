package com.demo.rippling;

import com.demo.rippling.model.EvaluatedExpense;
import com.demo.rippling.model.Expense;
import com.demo.rippling.rule.BlockExpenseTypeRule;
import com.demo.rippling.rule.MaxAmountRule;
import com.demo.rippling.rule.MaxCategoryAmountPerTripRule;
import com.demo.rippling.rule.MaxTripAmountRule;
import com.demo.rippling.rule.Rule;
import com.demo.rippling.rule.TripRule;
import com.demo.rippling.rule.VendorTypeMaxAmountRule;
import com.demo.rippling.service.RulesEngine;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // ── Per-expense rules ──────────────────────────────────────────────────
        List<Rule> rules = List.of(
                new BlockExpenseTypeRule("airfare"),
                new BlockExpenseTypeRule("entertainment"),
                new VendorTypeMaxAmountRule("restaurant", new BigDecimal("75")),
                new MaxAmountRule(new BigDecimal("250"))
        );

        // ── Trip-level rules ───────────────────────────────────────────────────
        List<TripRule> tripRules = List.of(
                new MaxTripAmountRule(new BigDecimal("2000")),
                new MaxCategoryAmountPerTripRule("meals", new BigDecimal("200"))
        );

        List<Map<String, String>> rawExpenses = List.of(

                // ── Trip t1: per-expense rule smoke tests ──────────────────────
                exp("001", "t1", "49.99",  "client_hosting", "restaurant"),  // APPROVED: restaurant < $75
                exp("002", "t1", "100.00", "client_hosting", "restaurant"),  // REJECTED: restaurant > $75
                exp("003", "t1", "200.00", "airfare",        "airline"),     // REJECTED: airfare blocked
                exp("004", "t1", "80.00",  "entertainment",  "venue"),       // REJECTED: entertainment blocked
                exp("005", "t1", "450.00", "client_hosting", "hotel"),       // REJECTED: > $250 cap
                exp("006", "t1", "75.00",  "meals",          "restaurant"),  // APPROVED: exactly $75, rule is >

                // ── Trip t2: trip total rule ($2000 cap, sequential) ───────────
                // 9 × $250 hotel. Each passes per-expense cap (rule is >, $250 is not > $250).
                // Running total: 8 × $250 = $2000 → still OK.
                // Expense t2-9 pushes to $2250 → trip rule fires. t2-1..t2-8 APPROVED, t2-9 REJECTED.
                exp("t2-1", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-2", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-3", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-4", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-5", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-6", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-7", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-8", "t2", "250.00", "accommodation", "hotel"),  // running total = $2000, passes
                exp("t2-9", "t2", "250.00", "accommodation", "hotel"),  // running total = $2250, REJECTED

                // ── Trip t3: meal category rule ($200 cap per trip) ────────────
                // vendor=cafeteria avoids the restaurant per-expense rule (restaurant cap is vendor-based).
                // t3-1: meals $120 → running $120, OK → APPROVED
                // t3-2: meals $100 → running $220 > $200 → trip rule fires → REJECTED
                // t3-3: hotel $50 → trip already violated → REJECTED (subsequent expense)
                exp("t3-1", "t3", "120.00", "meals",          "cafeteria"),
                exp("t3-2", "t3", "100.00", "meals",          "cafeteria"),
                exp("t3-3", "t3",  "50.00", "client_hosting", "hotel")
        );

        // ── Evaluate ───────────────────────────────────────────────────────────
        RulesEngine engine = new RulesEngine();
        List<Expense> expenses = rawExpenses.stream().map(Expense::fromMap).toList();
        List<EvaluatedExpense> results = engine.evaluateRules(rules, tripRules, expenses);

        // ── Print grouped by trip ──────────────────────────────────────────────
        String currentTrip = "";
        for (EvaluatedExpense r : results) {
            if (!r.getTripId().equals(currentTrip)) {
                currentTrip = r.getTripId();
                System.out.println("\n── Trip " + currentTrip + " ──");
            }
            System.out.printf("  [%-8s] expense=%-5s  $%-8s  %s%n",
                    r.getStatus(),
                    r.getExpenseId(),
                    r.getAmount(),
                    r.getReason() != null ? "→ " + r.getReason() : "");
        }
    }

    private static Map<String, String> exp(String id, String tripId, String amount,
                                            String expenseType, String vendorType) {
        return Map.ofEntries(
                Map.entry("expense_id",   id),
                Map.entry("trip_id",      tripId),
                Map.entry("amount_usd",   amount),
                Map.entry("expense_type", expenseType),
                Map.entry("vendor_type",  vendorType),
                Map.entry("vendor_name",  "vendor")
        );
    }
}
