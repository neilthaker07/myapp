package com.demo.expense.service;

import com.demo.expense.model.Expense;
import com.demo.expense.model.ExpenseCategory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Prep {
    public static void main(String[] args) {
        List<Expense> expenses = readFile("expenses.csv");
        for (Expense e : expenses) {
            System.out.println(e.getId());
        }

        Map<String, Map<String, BigDecimal>> expensesPerEmp = new HashMap<>();
        for (Expense expense : expenses) {
//            expensesPerEmp.put(expense.getEmployeeId(),
//                    expense.getAmount().add(expensesPerEmp.getOrDefault(expense.getEmployeeId(), BigDecimal.ZERO)));
            String tripId = expense.getTripId();
            BigDecimal amount = expense.getAmount();
            String employeeId = expense.getEmployeeId();

            Map<String, BigDecimal> existingTripsExpenses = !expensesPerEmp.containsKey(employeeId)
                    ? new HashMap<>() : expensesPerEmp.get(employeeId);
            existingTripsExpenses.put(tripId, amount.add(existingTripsExpenses.getOrDefault(tripId, BigDecimal.ZERO)));

            expensesPerEmp.put(expense.getEmployeeId(), existingTripsExpenses);
        }

//        Map<String, BigDecimal> expensesPerEmp = expenses.stream()
//                .collect(Collectors.groupingBy(
//                        Expense::getEmployeeId,
//                        LinkedHashMap::new,
//                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));

        System.out.println(expensesPerEmp);
    }

    // Reads a classpath CSV resource (header row + comma-separated fields) into a List<Expense>.
    static List<Expense> readFile(String classpathResource) {
        try (InputStream in = Prep.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalArgumentException("CSV resource not found: " + classpathResource);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return reader.lines()
                        .skip(1) // header row: id,employeeId,tripId,amount,category,merchant,date
                        .filter(line -> !line.isBlank())
                        .map(Prep::toExpense)
                        .toList();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Expense toExpense(String line) {
        String[] f = line.split(",");
        return Expense.builder()
                .id(f[0])
                .employeeId(f[1])
                .tripId(f[2])
                .amount(new BigDecimal(f[3]))
                .category(ExpenseCategory.valueOf(f[4]))
                .merchant(f[5])
                .date(LocalDate.parse(f[6]))
                .build();
    }
}
