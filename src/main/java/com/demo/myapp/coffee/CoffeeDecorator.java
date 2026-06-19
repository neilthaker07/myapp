package com.demo.myapp.coffee;

// Abstract Decorator — wraps a Coffee and delegates to it by default
// Subclasses override getDescription() and getCost() to add their contribution
public abstract class CoffeeDecorator implements Coffee {

    protected final Coffee wrapped;

    public CoffeeDecorator(Coffee wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }

    @Override
    public double getCost() {
        return wrapped.getCost();
    }
}
