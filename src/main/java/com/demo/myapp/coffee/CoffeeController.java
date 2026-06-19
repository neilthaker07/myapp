package com.demo.myapp.coffee;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coffee")
public class CoffeeController {

    // GET /api/coffee?add=milk,sugar,whip
    // Decorator Pattern — each add-on wraps the previous Coffee, stacking description and cost
    @GetMapping
    public ResponseEntity<Map<String, Object>> orderCoffee(
            @RequestParam(required = false, defaultValue = "") String add) {

        Coffee coffee = new SimpleCoffee(); // base — the Concrete Component

        // Wrap with a decorator for each requested add-on (order matters for description)
        for (String addon : add.split(",")) {
            coffee = switch (addon.trim().toLowerCase()) {
                case "milk"    -> new MilkDecorator(coffee);
                case "sugar"   -> new SugarDecorator(coffee);
                case "whip"    -> new WhipDecorator(coffee);
                case "vanilla" -> new VanillaDecorator(coffee);
                default        -> coffee; // unknown add-on — skip
            };
        }

        return ResponseEntity.ok(Map.of(
                "description", coffee.getDescription(),
                "cost", String.format("$%.2f", coffee.getCost())
        ));
    }
}
