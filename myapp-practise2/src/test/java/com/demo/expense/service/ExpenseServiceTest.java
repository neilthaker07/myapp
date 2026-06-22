package com.demo.expense.service;

import com.demo.expense.model.Expense;
import com.demo.expense.model.ExpenseCategory;
import com.demo.expense.rule.ExpenseRule;
import com.demo.expense.rule.ExpenseTripRule;
import com.demo.expense.rule.RuleField;
import com.demo.expense.rule.RuleOperator;
import com.demo.expense.rule.TripRuleField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseServiceTest {

    private ExpenseService service;

    @BeforeEach
    void setUp() {
        service = new ExpenseService();
    }

    private Expense buildExpense(String id, String amount, ExpenseCategory category, String merchant) {
        return buildExpense(id, "t1", amount, category, merchant);
    }

    private Expense buildExpense(String id, String tripId, String amount, ExpenseCategory category, String merchant) {
        return Expense.builder()
                .id(id)
                .employeeId("emp1")
                .tripId(tripId)
                .amount(new BigDecimal(amount))
                .category(category)
                .merchant(merchant)
                .date(LocalDate.of(2026, 6, 1))
                .build();
    }

    // ── No rules ──────────────────────────────────────────────────────────────

    @Test
    void evaluate_returnsEmpty_whenNoRulesAdded() {
        service.addExpense(buildExpense("e1", "600.00", ExpenseCategory.TRAVEL, "Marriott"));
        assertThat(service.evaluateExpenses()).isEmpty();
    }

    // ── No expenses ───────────────────────────────────────────────────────────

    @Test
    void evaluate_returnsEmpty_whenNoExpensesAdded() {
        service.addRule(new ExpenseRule(RuleField.AMOUNT, RuleOperator.GREATER_THAN, "500"));
        assertThat(service.evaluateExpenses()).isEmpty();
    }

    // ── Single rule ───────────────────────────────────────────────────────────

    @Test
    void evaluate_returnsViolatingExpense_whenAmountExceedsThreshold() {
        service.addRule(new ExpenseRule(RuleField.AMOUNT, RuleOperator.GREATER_THAN, "500"));
        service.addExpense(buildExpense("e1", "600.00", ExpenseCategory.TRAVEL, "Marriott"));

        List<Expense> violations = service.evaluateExpenses();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).getId()).isEqualTo("e1");
    }

    @Test
    void evaluate_returnsEmpty_whenNoExpenseViolatesRule() {
        service.addRule(new ExpenseRule(RuleField.AMOUNT, RuleOperator.GREATER_THAN, "500"));
        service.addExpense(buildExpense("e1", "400.00", ExpenseCategory.TRAVEL, "Marriott"));

        assertThat(service.evaluateExpenses()).isEmpty();
    }

    // ── Multiple expenses, only some violate ──────────────────────────────────

    @Test
    void evaluate_returnsOnlyViolatingExpenses_fromMixedList() {
        service.addRule(new ExpenseRule(RuleField.AMOUNT, RuleOperator.GREATER_THAN, "500"));
        service.addRule(new ExpenseRule(RuleField.AMOUNT, RuleOperator.LESS_THAN, "100"));
        service.addExpense(buildExpense("e1", "600.00", ExpenseCategory.TRAVEL, "Marriott"));  // violates
        service.addExpense(buildExpense("e2", "400.00", ExpenseCategory.MEALS,   "Chipotle")); // ok
        service.addExpense(buildExpense("e3", "750.00", ExpenseCategory.SOFTWARE, "AWS"));     // violates
        service.addExpense(buildExpense("e4", "50.00", ExpenseCategory.SOFTWARE, "AWS"));     // violates

        List<Expense> violations = service.evaluateExpenses();

        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(Expense::getId).containsExactlyInAnyOrder("e1", "e3", "e4");
    }

    // ── Multiple rules — any violation flags the expense ─────────────────────

    @Test
    void evaluate_flagsExpense_whenAnyRuleViolated() {
        service.addRule(new ExpenseRule(RuleField.AMOUNT,   RuleOperator.GREATER_THAN, "500"));
        service.addRule(new ExpenseRule(RuleField.CATEGORY, RuleOperator.EQUALS,       "MEALS"));

        // violates amount rule only
        service.addExpense(buildExpense("e1", "600.00", ExpenseCategory.TRAVEL, "Marriott"));
        // violates category rule only
        service.addExpense(buildExpense("e2", "30.00",  ExpenseCategory.MEALS,  "Chipotle"));
        // violates both rules
        service.addExpense(buildExpense("e3", "600.00", ExpenseCategory.MEALS,  "Chipotle"));
        // violates neither
        service.addExpense(buildExpense("e4", "200.00", ExpenseCategory.TRAVEL, "Delta"));

        List<Expense> violations = service.evaluateExpenses();

        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(Expense::getId).containsExactlyInAnyOrder("e1", "e2", "e3");
    }

    // ── Trip rules ────────────────────────────────────────────────────────────

    @Test
    void evaluateTrips_returnsViolatingTrip_whenTotalAmountExceedsThreshold() {
        service.addTripRule(new ExpenseTripRule(TripRuleField.TOTAL_AMOUNT, RuleOperator.GREATER_THAN, "1000"));

        // trip t1 total = 1100 → violates
        service.addExpense(buildExpense("e1", "t1", "600.00", ExpenseCategory.TRAVEL, "Marriott"));
        service.addExpense(buildExpense("e2", "t1", "500.00", ExpenseCategory.MEALS,   "Chipotle"));
        // trip t2 total = 700 → ok
        service.addExpense(buildExpense("e3", "t2", "400.00", ExpenseCategory.TRAVEL, "Delta"));
        service.addExpense(buildExpense("e4", "t2", "300.00", ExpenseCategory.MEALS,  "Chipotle"));

        Map<String, List<Expense>> violations = service.evaluateTrips();

        assertThat(violations).containsOnlyKeys("t1");
        assertThat(violations.get("t1")).extracting(Expense::getId).containsExactlyInAnyOrder("e1", "e2");
    }

    @Test
    void evaluateTrips_returnsEmpty_whenNoTripRulesAdded() {
        service.addExpense(buildExpense("e1", "t1", "600.00", ExpenseCategory.TRAVEL, "Marriott"));
        assertThat(service.evaluateTrips()).isEmpty();
    }

    @Test
    void evaluateTrips_flagsMultipleViolatingTrips() {
        service.addTripRule(new ExpenseTripRule(TripRuleField.EXPENSE_COUNT, RuleOperator.GREATER_THAN, "2"));

        // t1: 3 expenses → violates
        service.addExpense(buildExpense("e1", "t1", "100.00", ExpenseCategory.TRAVEL, "Delta"));
        service.addExpense(buildExpense("e2", "t1", "100.00", ExpenseCategory.MEALS,  "Chipotle"));
        service.addExpense(buildExpense("e3", "t1", "100.00", ExpenseCategory.MEALS,  "Chipotle"));
        // t2: 1 expense → ok
        service.addExpense(buildExpense("e4", "t2", "200.00", ExpenseCategory.TRAVEL, "Marriott"));
        // t3: 3 expenses → violates
        service.addExpense(buildExpense("e5", "t3", "50.00", ExpenseCategory.SOFTWARE, "AWS"));
        service.addExpense(buildExpense("e6", "t3", "50.00", ExpenseCategory.SOFTWARE, "AWS"));
        service.addExpense(buildExpense("e7", "t3", "50.00", ExpenseCategory.SOFTWARE, "AWS"));

        Map<String, List<Expense>> violations = service.evaluateTrips();

        assertThat(violations).containsOnlyKeys("t1", "t3");
    }

    // ── Merchant rule ─────────────────────────────────────────────────────────

    @Test
    void evaluate_flagsExpense_whenMerchantContainsRestrictedKeyword() {
        service.addRule(new ExpenseRule(RuleField.MERCHANT, RuleOperator.CONTAINS, "Airbnb"));
        service.addExpense(buildExpense("e1", "300.00", ExpenseCategory.TRAVEL, "Airbnb San Francisco"));
        service.addExpense(buildExpense("e2", "200.00", ExpenseCategory.TRAVEL, "Marriott"));

        List<Expense> violations = service.evaluateExpenses();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).getId()).isEqualTo("e1");
    }
}
