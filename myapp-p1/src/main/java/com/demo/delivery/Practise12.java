package com.demo.delivery;

import java.util.HashMap;
import java.util.Map;

record Employee1(int id, String name, int salary) {}

public class Practise12 {
    static Map<String, Map<String, Integer>> map = new HashMap<>();
    static Map<String, Map<Integer, Employee1>> departments = new HashMap<>();
    public static void main(String[] args) {
//        addEmployee("Eng", "Alex", 1000);
//        addEmployee("HR", "Bob", 2000);
//        System.out.println(map);
//        increaseSalary("Eng", "Alex", 500);
//        System.out.println(map);
//        removeEmployee("HR", "Charles");
//        System.out.println(map);
        addEmployee("Eng", new Employee1(1, "Alex", 1000));
        addEmployee("HR", new Employee1(2, "Bob", 5000));
        increaseSalary("Eng", 1, 500);
        System.out.println(departments);
    }

    static void addEmployee(String department, Employee1 employee) {
        departments.computeIfAbsent(department, k -> new HashMap<>()).putIfAbsent(employee.id(), employee);
    }

    static void increaseSalary(String department, int employeeId, int increment) {
        Map<Integer, Employee1> dep = departments.get(department);
        if (dep != null) {
            Employee1 employee1 = dep.get(employeeId);
            if (employee1 != null) {
                dep.put(employeeId, new Employee1(employeeId, employee1.name(), employee1.salary() + increment));
            }
        }
    }

    static void addEmployee(String department, String name, int salary) {
        map.computeIfAbsent(department, k -> new HashMap<>()).putIfAbsent(name, salary);
    }

    static void increaseSalary(String department, String name, int increment) {
        Map<String, Integer> employees = map.get(department);

        if (employees != null) {
            employees.computeIfPresent(name, (key, salary) -> salary + increment);
        }
//        map.computeIfAbsent(department, k -> new HashMap<>()).put(name, map.get(department).get(name) + increment);
    }

    static void removeEmployee(String department, String name) {
        Map<String, Integer> employees = map.get(department);

        if (employees != null) {
            employees.remove(name);

            if (employees.isEmpty()) {
                map.remove(department);
            }
        }

        if (map.containsKey(department)) {
            Map<String, Integer> emp = map.get(department);
            if (emp.containsKey(name)) {
                emp.remove(name);
                if (emp.isEmpty()) {
                    map.remove(department);
                }
            }
        }
    }
}
