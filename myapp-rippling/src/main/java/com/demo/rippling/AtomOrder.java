package com.demo.rippling;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Getter
//@Setter
//class Order {
//    List<OrderItem> orderItems;
//    Order() {
//        orderItems = new ArrayList<>();
//    }
//    Order(List<OrderItem> orderItems) {
//        this.orderItems = orderItems;
//    }
//}
//
//@Getter
//@Setter
//class OrderItem {
//    String orderId;
//    Integer qty;
//    Double price;
//    OrderItem(String orderId, Integer qty, Double price) {
//        this.orderId = orderId;
//        this.qty = qty;
//        this.price = price;
//    }
//
//    @Override
//    public String toString() {
//        return this.orderId + " " + this.qty + " " + this.price;
//    }
//}

class OrderCreator {
    Map<String, Integer> orderItems = new HashMap();
    public void createOrder(String itemId) {
        orderItems.put(itemId, orderItems.getOrDefault(itemId, 0) + 1);
    }

    public Map<String, Integer> getOrder() {
        return orderItems;
    }
}

public class AtomOrder {
    public static void main(String[] args) {
        OrderCreator creator = new OrderCreator();
        kiosk(creator);
        Map<String, Integer> orders = creator.getOrder();
        Map<String, Double> menu = Map.of("burger", 5.99, "fries", 3.99, "soda", 1.99);
        double total = 0.0;
        for (Map.Entry<String, Integer> order : orders.entrySet()) {
            String orderId = order.getKey();
            Integer qty = order.getValue();
            double price = menu.get(orderId) * qty;
            total += price;
            System.out.println(orderId + " " + qty + " " + price);
        }
        System.out.println("total " + total);
    }

    static void kiosk(OrderCreator creator) {
        creator.createOrder("burger");
        creator.createOrder("burger");
        creator.createOrder("fries");
        creator.createOrder("soda");
    }
}
