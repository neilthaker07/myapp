package com.demo.rippling.controller;

import com.demo.rippling.model.EvaluatedExpense;
import com.demo.rippling.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * POST /api/expenses/evaluate
     * Accepts raw expense maps matching the problem's input format:
     * [{ "expense_id": "001", "trip_id": "001", "amount_usd": "49.99", "expense_type": "...", "vendor_type": "...", "vendor_name": "..." }]
     */
    @PostMapping("/evaluate")
    public List<EvaluatedExpense> evaluate(@RequestBody List<Map<String, String>> expenses) {
        return expenseService.evaluate(expenses);
    }
}
