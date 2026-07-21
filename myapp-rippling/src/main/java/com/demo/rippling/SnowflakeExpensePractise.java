package com.demo.rippling;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * PROBLEM: Employee Expense Reimbursement Engine
 *
 * You are building the backend core for an HR expense approval system.
 * The company has a monthly expense policy that dictates the maximum allowable
 * reimbursement per category for every employee.
 *
 * Example Policy:
 *   TRAVEL: $1000.00
 *   MEALS:  $200.00
 *   IT:     $500.00
 *
 * Employees submit expense requests one at a time over the course of the month.
 *
 * Implement an Engine that processes these requests:
 *   1. Accept an expense submission (Employee ID, Category, Amount).
 *   2. Evaluate the request:
 *        - If approving the expense keeps the employee's monthly total for that
 *          category AT or BELOW the policy limit, mark it APPROVED and update their total.
 *        - If it exceeds the remaining limit for that category, mark it REJECTED
 *          (do not add to their total).
 *   3. Generate an "End of Month Statement" for a specific employee that prints:
 *        - Total approved amount across all categories.
 *        - Remaining budget for each category.
 *        - A list of all REJECTED expense amounts and their categories.
 *
 * Example Flow:
 *   Limits: MEALS = $200.00
 *   - Alice submits MEALS $150.00 -> APPROVED (Remaining: $50)
 *   - Alice submits MEALS $75.00  -> REJECTED (Would exceed by $25)
 *   - Alice submits MEALS $40.00  -> APPROVED (Remaining: $10)
 *
 * Edge cases to think about:
 *   - An employee submits an expense for a category that doesn't exist in the policy.
 *   - Processing negative expense amounts (refunds) — should this replenish the budget?
 *   - An employee requesting a statement who hasn't submitted any expenses yet.
 *
 * Constraints / things to reason about out loud (interview framing):
 *   - Thread safety: What if Alice submits two expenses at the exact same millisecond
 *     from two different devices? How do you prevent a race condition exceeding the budget?
 *   - OOD: Where should the state of "current totals" live? (e.g., Engine vs. Employee class).
 */
public class SnowflakeExpensePractise {

    public enum Category {
        TRAVEL, MEALS, IT, OFFICE_SUPPLIES
    }

    public static void main(String[] args) {
        // 1. Initialize the company policy
        Map<Category, Double> companyPolicy = Map.of(
                Category.TRAVEL, 1000.0,
                Category.MEALS, 200.0,
                Category.IT, 500.0
        );

        ExpenseEngine engine = new ExpenseEngine(companyPolicy);

        // 2. Simulate streaming expense submissions
        System.out.println(engine.submitExpense("EMP-01", Category.MEALS, 150.0)); // APPROVED
        System.out.println(engine.submitExpense("EMP-01", Category.MEALS, 75.0));  // REJECTED
        System.out.println(engine.submitExpense("EMP-02", Category.IT, 400.0));    // APPROVED
        System.out.println(engine.submitExpense("EMP-01", Category.MEALS, 40.0));  // APPROVED
        System.out.println(engine.submitExpense("EMP-01", Category.TRAVEL, 500.0));// APPROVED

        System.out.println("\n--- EMP-01 Monthly Statement ---");
        // TODO: Generate and print the statement
         engine.printStatement("EMP-01");
    }

    @Getter
    @AllArgsConstructor
    static class ExpenseItem {
        private Category category;
        private Double amount;
        private ExpenseStatus expenseStatus;
    }

    enum ExpenseStatus {
        APPROVED,
        REJECTED,
        PENDING
    }

    @Getter
    static class EmployeeAccount {
        private final String employeeId;
        private final List<ExpenseItem> history = new ArrayList<>();
        private final Map<Category, Double> approvedTotals = new HashMap<>();

        EmployeeAccount(String employeeId) {
            this.employeeId = employeeId;
        }

        public synchronized ExpenseStatus processExpense(Category category, double amount, double policyLimit) {
            double currentTotal = approvedTotals.getOrDefault(category, 0.0);

            if (currentTotal + amount <= policyLimit) {
                approvedTotals.put(category, currentTotal + amount);
                history.add(new ExpenseItem(category, amount, ExpenseStatus.APPROVED));
                return ExpenseStatus.APPROVED;
            } else {
                history.add(new ExpenseItem(category, amount, ExpenseStatus.REJECTED));
                return ExpenseStatus.REJECTED;
            }
        }
    }

    static class ExpenseEngine {
        private final Map<Category, Double> policyLimits;
        // Hint: You will need a way to store employee data.
        private final Map<String, EmployeeAccount> employeeAccounts = new ConcurrentHashMap<>();

        public ExpenseEngine(Map<Category, Double> policyLimits) {
            this.policyLimits = policyLimits;
        }

        public ExpenseStatus submitExpense(String employeeId, Category category, double amount) {
            Double limit = policyLimits.get(category);
            if (limit == null) {
                return ExpenseStatus.REJECTED;
            }
            EmployeeAccount account = employeeAccounts.computeIfAbsent(employeeId, EmployeeAccount::new);
            return account.processExpense(category, amount, limit);
        }

        public void printStatement(String employeeId) {
            // Implement statement generation here
            EmployeeAccount employeeAccount = employeeAccounts.get(employeeId);
            List<ExpenseItem> history =employeeAccount.getHistory();
            for (ExpenseItem er: history) {
                System.out.println(employeeId + " " + er.getCategory() + " " + er.getAmount() + " " + er.getExpenseStatus());
            }
        }
    }

    // Feel free to create helper classes like EmployeeRecord, ExpenseItem, etc.
}