package com.demo.myapp.coffee;

// Decorator Pattern — Component interface: every coffee (base and decorated) implements this
public interface Coffee {
    String getDescription();
    double getCost();
}
