package com.demo.delivery;

import java.util.*;

record Employee(String name, String department) {}

public class Practise11 {
    public static void main(String[] args) {
        System.out.println("testing");
        List<Employee> employees = List.of(
                new Employee("Alice", "Engineering"),
                new Employee("Bob", "Finance"),
                new Employee("Charlie", "Engineering"),
                new Employee("Diana", "Finance"),
                new Employee("Eve", "HR")
        );
        Map<String, Set<String>> map = new HashMap<>();
        for (Employee e : employees) {
            map.computeIfAbsent(e.department(), k -> new HashSet<>()).add(e.name());
        }
        System.out.println(map); // <Engineer, [Alice, Charlie]> <HR, [Eve]>
        moveEmployee(map, "Alice", "Engineering", "HR");
        moveEmployee(map, "Charlie", "Engineering", "Finance");
        System.out.println(map);
    }

    static void moveEmployee(Map<String, Set<String>> map, String name, String from, String to) {
        if (from.equals(to)) {
            return;
        }

        if (!map.containsKey(from)) {
            return;
        }

        if (!map.get(from).contains(name)) {
            return;
        }

        Set<String> fromEmployees = map.get(from);
        fromEmployees.remove(name);

        if (fromEmployees.size() == 0) {
            map.remove(from);
        }

        map.computeIfAbsent(to, k -> new HashSet<>()).add(name);
    }
}
