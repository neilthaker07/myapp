package com.demo.myapp.coffee;

public class VanillaDecorator extends CoffeeDecorator {

    public VanillaDecorator(Coffee wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + ", Vanilla";
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + 0.30;
    }
}
