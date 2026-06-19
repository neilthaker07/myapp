package com.demo.myapp.car;

// Concrete Factory — Prius family: HybridEngine + HatchbackBody (always compatible)
public class PriusFactory extends CarFactory {

    @Override
    protected Engine getEngine() { return new HybridEngine(); }

    @Override
    protected Body getBody() { return new HatchbackBody(); }
}
