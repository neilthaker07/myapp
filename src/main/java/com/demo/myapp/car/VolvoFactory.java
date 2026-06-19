package com.demo.myapp.car;

// Concrete Factory — Volvo family: GasEngine + SUVBody (always compatible)
public class VolvoFactory extends CarFactory {

    @Override
    protected Engine getEngine() { return new GasEngine(); }

    @Override
    protected Body getBody() { return new SUVBody(); }
}
