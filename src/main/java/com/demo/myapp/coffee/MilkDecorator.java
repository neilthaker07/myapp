package com.demo.myapp.coffee;

public class MilkDecorator extends CoffeeDecorator {

    public MilkDecorator(Coffee wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + ", Milk";
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + 0.25;
    }
}
