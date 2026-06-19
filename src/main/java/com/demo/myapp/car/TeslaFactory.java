package com.demo.myapp.car;

// Concrete Factory — Tesla family: ElectricEngine + SedanBody (always compatible)
public class TeslaFactory extends CarFactory {

    @Override
    protected Engine getEngine() { return new ElectricEngine(); }

    @Override
    protected Body getBody() { return new SedanBody(); }
}
