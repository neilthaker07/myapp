package com.demo.rippling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * PROBLEM: Account Transaction Ledger
 *
 * You're given a list of bank transactions, already in chronological order:
 *
 *   record Transaction(String accountId, TransactionType type, double amount) {}
 *   enum TransactionType { DEBIT, CREDIT }
 *
 * Example input (in this order):
 *   ("A1", CREDIT, 100.00)
 *   ("A2", CREDIT, 50.00)
 *   ("A1", DEBIT,  30.00)
 *   ("A1", DEBIT,  90.00)   // A1 balance: 100 - 30 - 90 = -20  (goes negative here)
 *   ("A2", DEBIT,  20.00)
 *   ("A1", CREDIT, 25.00)   // A1 balance: -20 + 25 = 5        (back positive)
 *
 * Implement, iterating the list exactly once in order:
 *   1. Track a running balance per account as you go (CREDIT adds, DEBIT subtracts).
 *   2. The moment any account's balance goes negative, record that fact — which
 *      account, and at which transaction index it happened. An account can go
 *      negative and recover; record every time it *crosses* from >= 0 into < 0.
 *   3. After processing all transactions, print:
 *        - each account's final balance
 *        - every negative-balance event you recorded (account + index)
 *
 * Expected output for the example above (order of accounts doesn't matter):
 *   Final balances: A1=5.00, A2=30.00
 *   Went negative: A1 at index 3
 *
 * Edge cases to think about:
 *   - An account that never goes negative — nothing should be reported for it.
 *   - An account that goes negative more than once (dips below zero, recovers,
 *     dips below zero again) — should that count as two separate events?
 *   - Empty transaction list.
 *   - A transaction amount of 0 — does it ever trigger a "went negative" event?
 *
 * Constraints / things to reason about out loud (interview framing):
 *   - This must be a single pass over the list — don't re-iterate per account.
 *     What data structure lets you look up "this account's running balance so
 *     far" in O(1) as you walk the list once?
 *   - Why does processing order matter here, unlike the kiosk-receipt problem
 *     (where summing quantities per item didn't care what order selections
 *     arrived in)?
 *   - How would you extend this to flag an account the moment it goes negative,
 *     in real time, rather than only reporting it after processing the full
 *     batch — where would that hook go in a real Spring Boot service?
 */
public class TransactionLedgerPractice {

    enum TransactionType { DEBIT, CREDIT }

    record Transaction(String accountId, TransactionType type, double amount) {}

    // *   Final balances: A1=5.00, A2=30.00
    // *   Went negative: A1 at index 3
    public static void main(String[] args) {
        List<Transaction> transactions = List.of(
                new Transaction("A1", TransactionType.CREDIT, 100.00),
                new Transaction("A2", TransactionType.CREDIT, 50.00),
                new Transaction("A1", TransactionType.DEBIT, 30.00),
                new Transaction("A1", TransactionType.DEBIT, 90.00),
                new Transaction("A2", TransactionType.DEBIT, 20.00),
                new Transaction("A1", TransactionType.CREDIT, 25.00)
        );

        Map<String, Double> accountsBal = new HashMap<>();
        Map<String, List<Integer>> accountsBalNegative = new HashMap<>();
        for (int i=0; i< transactions.size(); i++) {
            Transaction txn = transactions.get(i);
            String accountId = txn.accountId;
            TransactionType type = txn.type;
            double amount = 0.0;
            if (type == TransactionType.CREDIT) {
                amount = txn.amount;
            } else if (type == TransactionType.DEBIT) {
                amount = -1 * txn.amount;
            }

            double tmp = 0.0;
            if (accountsBal.containsKey(accountId)) {
                tmp = accountsBal.get(accountId);
                tmp += amount;
                accountsBal.put(accountId, tmp);
            } else {
                tmp = amount;
                accountsBal.put(accountId, amount);
            }

//            accountsBal.put(accountId, accountsBal.getOrDefault(accountId, 0.0) + amount);

            if (tmp < 0 && tmp-amount >= 0) {
//                if (accountsBalNegative.containsKey(accountId)) {
//                    List<Integer> indexes = accountsBalNegative.get(accountId);
//                    indexes.add(i);
//                    accountsBalNegative.put(accountId, indexes);
//                } else {
//                    List<Integer> indexes = new ArrayList<>();
//                    indexes.add(i);
//                    accountsBalNegative.put(accountId, indexes);
//                }
                accountsBalNegative.computeIfAbsent(accountId, k -> new ArrayList<>()).add(i);
            }

        }

        System.out.println("total balance per account " + accountsBal);
        System.out.println("account going into negative " + accountsBalNegative);

        // TODO: iterate transactions once, track running balance per account,
        // and record each time an account's balance crosses from >= 0 into < 0.

        // TODO: print final balances per account.

        // TODO: print every negative-balance crossing event (account + index).
    }
}
