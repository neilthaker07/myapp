package com.demo.myapp.coffee;

public class SugarDecorator extends CoffeeDecorator {

    public SugarDecorator(Coffee wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + ", Sugar";
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + 0.10;
    }
}
