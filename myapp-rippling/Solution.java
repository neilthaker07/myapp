import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

public class Solution {

    // ── Models ─────────────────────────────────────────────────────────────────

    enum EvaluationStatus { APPROVED, REJECTED }

    static class Expense {
        private final String expenseId;
        private final String tripId;
        private final BigDecimal amount;
        private final String expenseType;
        private final String vendorType;
        private final String vendorName;

        Expense(String expenseId, String tripId, BigDecimal amount,
                String expenseType, String vendorType, String vendorName) {
            this.expenseId = expenseId;
            this.tripId    = tripId;
            this.amount    = amount;
            this.expenseType = expenseType;
            this.vendorType  = vendorType;
            this.vendorName  = vendorName;
        }

        String getExpenseId()  { return expenseId; }
        String getTripId()     { return tripId; }
        BigDecimal getAmount() { return amount; }
        String getExpenseType(){ return expenseType; }
        String getVendorType() { return vendorType; }
        String getVendorName() { return vendorName; }

        static Expense fromMap(Map<String, String> m) {
            return new Expense(
                    m.get("expense_id"), m.get("trip_id"),
                    new BigDecimal(m.getOrDefault("amount_usd", "0")),
                    m.getOrDefault("expense_type", ""),
                    m.getOrDefault("vendor_type", ""),
                    m.getOrDefault("vendor_name", ""));
        }
    }

    static class EvaluatedExpense {
        private final String expenseId;
        private final String tripId;
        private final EvaluationStatus status;
        private final BigDecimal amount;
        private final String reason; // null when APPROVED

        EvaluatedExpense(String expenseId, String tripId, EvaluationStatus status,
                         BigDecimal amount, String reason) {
            this.expenseId = expenseId;
            this.tripId    = tripId;
            this.status    = status;
            this.amount    = amount;
            this.reason    = reason;
        }

        String getExpenseId()      { return expenseId; }
        String getTripId()         { return tripId; }
        EvaluationStatus getStatus(){ return status; }
        BigDecimal getAmount()     { return amount; }
        String getReason()         { return reason; }

        static EvaluatedExpense approved(Expense e) {
            return new EvaluatedExpense(e.getExpenseId(), e.getTripId(), EvaluationStatus.APPROVED, e.getAmount(), null);
        }

        static EvaluatedExpense rejected(Expense e, String reason) {
            return new EvaluatedExpense(e.getExpenseId(), e.getTripId(), EvaluationStatus.REJECTED, e.getAmount(), reason);
        }
    }

    // ── Rule interfaces ────────────────────────────────────────────────────────

    @FunctionalInterface
    interface Rule {
        Optional<String> evaluate(Expense expense);
    }

    @FunctionalInterface
    interface TripRuleEvaluator {
        // Called once per expense in trip order; maintains running state internally
        Optional<String> check(Expense expense);
    }

    interface TripRule {
        // Returns a fresh evaluator per trip — each trip starts with zero running totals
        TripRuleEvaluator newEvaluator();
    }

    // ── Per-expense rule implementations ──────────────────────────────────────

    // "No expenses over $250"
    static class MaxAmountRule implements Rule {
        private final BigDecimal max;
        MaxAmountRule(BigDecimal max) { this.max = max; }

        public Optional<String> evaluate(Expense e) {
            return e.getAmount().compareTo(max) > 0
                    ? Optional.of("Expense $" + e.getAmount().toPlainString() + " exceeds per-expense limit of $" + max.toPlainString())
                    : Optional.empty();
        }
    }

    // "No airfare expenses" / "No entertainment expenses"
    static class BlockExpenseTypeRule implements Rule {
        private final String blockedType;
        BlockExpenseTypeRule(String blockedType) { this.blockedType = blockedType; }

        public Optional<String> evaluate(Expense e) {
            return e.getExpenseType().equalsIgnoreCase(blockedType)
                    ? Optional.of("Expense type '" + blockedType + "' is not allowed")
                    : Optional.empty();
        }
    }

    // "No restaurant expense can exceed $75"
    static class VendorTypeMaxAmountRule implements Rule {
        private final String vendorType;
        private final BigDecimal max;
        VendorTypeMaxAmountRule(String vendorType, BigDecimal max) { this.vendorType = vendorType; this.max = max; }

        public Optional<String> evaluate(Expense e) {
            if (!e.getVendorType().equalsIgnoreCase(vendorType)) return Optional.empty();
            return e.getAmount().compareTo(max) > 0
                    ? Optional.of("'" + vendorType + "' expenses cannot exceed $" + max.toPlainString()
                            + " (submitted: $" + e.getAmount().toPlainString() + ")")
                    : Optional.empty();
        }
    }

    // ── Trip rule implementations ──────────────────────────────────────────────

    // "A trip cannot exceed $2000 in total expenses"
    // Sequential: expenses 1-8 at $250 = $2000 (OK); expense 9 → $2250 → fires
    static class MaxTripAmountRule implements TripRule {
        private final BigDecimal max;
        MaxTripAmountRule(BigDecimal max) { this.max = max; }

        public TripRuleEvaluator newEvaluator() {
            // BigDecimal[] array: standard trick to mutate state inside a lambda
            // (lambda needs effectively-final capture; array ref is final, contents mutate)
            BigDecimal[] running = {BigDecimal.ZERO};
            return expense -> {
                running[0] = running[0].add(expense.getAmount());
                return running[0].compareTo(max) > 0
                        ? Optional.of("Trip total $" + running[0].toPlainString() + " exceeds limit of $" + max.toPlainString())
                        : Optional.empty();
            };
        }
    }

    // "Total meal expenses cannot exceed $200 per trip"
    // Only accumulates when expense_type matches; non-matching types pass through
    static class MaxCategoryAmountPerTripRule implements TripRule {
        private final String expenseType;
        private final BigDecimal max;
        MaxCategoryAmountPerTripRule(String expenseType, BigDecimal max) { this.expenseType = expenseType; this.max = max; }

        public TripRuleEvaluator newEvaluator() {
            BigDecimal[] running = {BigDecimal.ZERO};
            return expense -> {
                if (!expense.getExpenseType().equalsIgnoreCase(expenseType)) return Optional.empty();
                running[0] = running[0].add(expense.getAmount());
                return running[0].compareTo(max) > 0
                        ? Optional.of("Trip '" + expenseType + "' total $" + running[0].toPlainString()
                                + " exceeds limit of $" + max.toPlainString())
                        : Optional.empty();
            };
        }
    }

    // ── Rules engine ───────────────────────────────────────────────────────────

    static List<EvaluatedExpense> evaluateRules(List<Rule> rules, List<TripRule> tripRules,
                                                 List<Map<String, String>> rawExpenses) {
        List<Expense> expenses = rawExpenses.stream().map(Expense::fromMap).toList();

        // Group by tripId; LinkedHashMap preserves the order trips first appear in input
        Map<String, List<Expense>> byTrip = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getTripId, LinkedHashMap::new, Collectors.toList()));

        Map<String, EvaluatedExpense> resultById = new LinkedHashMap<>();
        byTrip.forEach((tripId, tripExpenses) ->
                evaluateTrip(rules, tripRules, tripExpenses)
                        .forEach(r -> resultById.put(r.getExpenseId(), r)));

        // Return in original input order
        return expenses.stream().map(e -> resultById.get(e.getExpenseId())).toList();
    }

    private static List<EvaluatedExpense> evaluateTrip(List<Rule> rules, List<TripRule> tripRules,
                                                        List<Expense> tripExpenses) {
        List<TripRuleEvaluator> evaluators = tripRules.stream().map(TripRule::newEvaluator).toList();

        List<EvaluatedExpense> results = new ArrayList<>();
        boolean tripViolated = false;
        String tripViolationReason = null;

        for (Expense expense : tripExpenses) {

            // Per-expense rules — checked first, independent of trip state
            Optional<String> expenseViolation = rules.stream()
                    .map(r -> r.evaluate(expense))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();

            if (expenseViolation.isPresent()) {
                // Blocked expenses don't count toward trip running totals (card was never charged)
                results.add(EvaluatedExpense.rejected(expense, expenseViolation.get()));
                continue;
            }

            // Trip already violated — reject all subsequent without re-checking evaluators
            if (tripViolated) {
                results.add(EvaluatedExpense.rejected(expense, tripViolationReason));
                continue;
            }

            // Feed to trip evaluators — first one to fire rejects this expense
            Optional<String> tripViolation = evaluators.stream()
                    .map(ev -> ev.check(expense))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();

            if (tripViolation.isPresent()) {
                tripViolated = true;
                tripViolationReason = tripViolation.get();
                results.add(EvaluatedExpense.rejected(expense, tripViolationReason));
            } else {
                results.add(EvaluatedExpense.approved(expense));
            }
        }
        return results;
    }

    // ── Main ───────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        List<Rule> rules = List.of(
                new BlockExpenseTypeRule("airfare"),
                new BlockExpenseTypeRule("entertainment"),
                new VendorTypeMaxAmountRule("restaurant", new BigDecimal("75")),
                new MaxAmountRule(new BigDecimal("250"))
        );

        List<TripRule> tripRules = List.of(
                new MaxTripAmountRule(new BigDecimal("2000")),
                new MaxCategoryAmountPerTripRule("meals", new BigDecimal("200"))
        );

        List<Map<String, String>> expenses = List.of(
                // ── Trip t1: per-expense rules ───────────────────────────────
                exp("001", "t1", "49.99",  "client_hosting", "restaurant"),  // APPROVED: restaurant < $75
                exp("002", "t1", "100.00", "client_hosting", "restaurant"),  // REJECTED: restaurant > $75
                exp("003", "t1", "200.00", "airfare",        "airline"),     // REJECTED: airfare blocked
                exp("004", "t1", "80.00",  "entertainment",  "venue"),       // REJECTED: entertainment blocked
                exp("005", "t1", "450.00", "client_hosting", "hotel"),       // REJECTED: > $250 cap
                exp("006", "t1", "75.00",  "meals",          "restaurant"),  // APPROVED: exactly $75 (rule is >)

                // ── Trip t2: trip total cap ($2000) ──────────────────────────
                // 9 × $250 hotel; each passes per-expense cap (250 is NOT > 250)
                // t2-1..t2-8 = $2000 running total → still OK; t2-9 → $2250 → REJECTED
                exp("t2-1", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-2", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-3", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-4", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-5", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-6", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-7", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-8", "t2", "250.00", "accommodation", "hotel"),
                exp("t2-9", "t2", "250.00", "accommodation", "hotel"),

                // ── Trip t3: meal category cap ($200) ────────────────────────
                // vendor=cafeteria avoids restaurant per-expense rule
                // t3-1: meals $120 → running $120, OK → APPROVED
                // t3-2: meals $100 → running $220 > $200 → trip rule fires → REJECTED
                // t3-3: hotel $50 → trip already violated → REJECTED
                exp("t3-1", "t3", "120.00", "meals",          "cafeteria"),
                exp("t3-2", "t3", "100.00", "meals",          "cafeteria"),
                exp("t3-3", "t3",  "50.00", "client_hosting", "hotel")
        );

        List<EvaluatedExpense> results = evaluateRules(rules, tripRules, expenses);

        String currentTrip = "";
        for (EvaluatedExpense r : results) {
            if (!r.getTripId().equals(currentTrip)) {
                currentTrip = r.getTripId();
                System.out.println("\n── Trip " + currentTrip + " ──");
            }
            System.out.printf("  [%-8s] expense=%-5s  $%-8s  %s%n",
                    r.getStatus(), r.getExpenseId(), r.getAmount().toPlainString(),
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
