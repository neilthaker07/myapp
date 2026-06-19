package com.demo.myapp.car;

public class ElectricEngine implements Engine {

    @Override
    public String getType() { return "Electric"; }

    @Override
    public String start() { return "Electric motor started — silent and instant torque"; }
}
