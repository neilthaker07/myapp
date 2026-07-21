package com.demo.rippling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * PROBLEM: Kiosk Order Receipt
 *
 * You're building the backend for a self-service food kiosk.
 *
 * You are given a menu as a Map<String, Double> of item name -> unit price, e.g.:
 *   { "burger": 5.99, "fries": 3.99, "soda": 1.99 }
 *
 * Customers place an order by selecting items one at a time (a customer can select
 * the same item multiple times, e.g. two burgers = "burger" selected twice).
 *
 * Implement:
 *   1. A way to record each item selection as it comes in, one at a time
 *      (i.e. don't require the whole order up front — items arrive one call at a time,
 *      like a kiosk button press).
 *   2. Once the order is complete, produce a receipt: for each distinct item ordered,
 *      print the item name, quantity ordered, and the line total (qty * unit price).
 *   3. Print a grand total across all line items at the end.
 *
 * Example:
 *   Selections: burger, burger, fries, soda
 *   Menu: burger=5.99, fries=3.99, soda=1.99
 *
 *   Expected output (order of lines doesn't matter):
 *     burger 2 11.98
 *     fries 1 3.99
 *     soda 1 1.99
 *     total 17.96
 *
 * Edge cases to think about:
 *   - An item selected that isn't on the menu — what should happen?
 *   - No items selected at all — what should the receipt look like?
 *   - Same item selected many times — make sure quantity aggregates correctly.
 *
 * Constraints / things to reason about out loud (interview framing):
 *   - What's the time complexity of building the receipt relative to number of
 *     selections and number of distinct items?
 *   - Where would this logic live in a real Spring Boot service — controller,
 *     service, or a dedicated aggregator class? Why?
 */
public class AtomOrderPractice {

    public static void main(String[] args) {
        Map<String, Double> menu = Map.of("burger", 5.99, "fries", 3.99, "soda", 1.99);
        Order order = new Order();
        // TODO: record selections one at a time, e.g. via kiosk(...) below
        kiosk("burger", order, menu);
        kiosk( "soda", order, menu);
        kiosk( "fries", order, menu);
        kiosk( "burger", order, menu);

        // TODO: build and print the receipt + grand total
        Map<String, Integer> items = order.getItems();
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            double p = item.getValue() * menu.get(item.getKey());
            System.out.println(item.getKey() + " " + item.getValue() + " " + p);
        }
        System.out.println(order.getTotal(menu));
    }

    static class Order {
        private final Map<String, Integer> items = new HashMap<>();

        void addItem(String item, Map<String, Double> menu) {
            if (!menu.containsKey(item)) throw new IllegalArgumentException("Unknown menu item: " + item);
            items.merge(item, 1, Integer::sum);
        }

        Map<String, Integer> getItems() {
            return Collections.unmodifiableMap(items);
        }

        double getTotal(Map<String, Double> menu) {
            return items.entrySet().stream()
                    .mapToDouble(e -> e.getValue() * menu.get(e.getKey()))
                    .sum();
        }
    }

    static void kiosk(String item, Order order, Map<String, Double>  menu) {
        // validate the item
        if (!menu.containsKey(item)) {
            throw new IllegalArgumentException("Unknown menu item: " + item);
        }

        // Simulates a customer pressing kiosk buttons one at a time.
        // TODO: wire this up to whatever order-recording mechanism you build above.
//        Map<String, Integer> items = order.getItems();
//        items.put(item, items.getOrDefault(item, 0) + 1);
        order.addItem(item, menu);
    }
}
