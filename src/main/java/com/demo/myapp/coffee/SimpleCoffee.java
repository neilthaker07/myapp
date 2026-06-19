package com.demo.myapp.coffee;

// Concrete Component — the base coffee that gets decorated
public class SimpleCoffee implements Coffee {

    @Override
    public String getDescription() {
        return "Simple Coffee";
    }

    @Override
    public double getCost() {
        return 1.00;
    }
}
