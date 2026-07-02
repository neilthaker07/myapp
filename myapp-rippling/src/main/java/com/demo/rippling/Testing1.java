package com.demo.rippling;

import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Testing1 {

    public static void main(String[] args) {
        List<String> items = List.of("123", "sdf", "sdgfsdfsdfgfsdffz", "4rf");

        List<String> ff = items.stream()
                .filter(item -> item.length() < 5)
                .map(item -> item+"_PASSING")
                .toList();

        System.out.println(ff);
        System.out.println(loadProductsFromCsv());
//        List<Subscription> subs = loadProductsFromCsv();
//        System.out.println(subs.stream().map(sub -> System.out.printf(sub.)));
    }


    static List<Subscription> loadProductsFromCsv() {
        final String subscription = """
            id, planId
            acme, STARTER
            globex, GROWTH
            """;
        return Arrays.stream(subscription.strip().split("\n"))
                .skip(1)                    // header row
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(s -> {
                    Subscription ss =  new Subscription();
                    return ss.fromCsv(s);
                })
                .toList();
    }
}

@Getter
class Subscription {
    String id;
    String planId;

    Subscription() {
    }

    Subscription(String id, String planId) {
        this.id = id;
        this.planId = planId;
    }

    @Override
    public String toString() {
        return id + " " + planId;
    }

    Subscription fromCsv(String line) {
        String[] c = line.split(",");
        return new Subscription(c[0].trim(), c[1].trim());
    }
}