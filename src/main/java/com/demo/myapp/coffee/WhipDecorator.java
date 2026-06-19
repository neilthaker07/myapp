package com.demo.myapp.coffee;

public class WhipDecorator extends CoffeeDecorator {

    public WhipDecorator(Coffee wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + ", Whip";
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + 0.50;
    }
}
