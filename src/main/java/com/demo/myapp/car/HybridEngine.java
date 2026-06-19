package com.demo.myapp.car;

public class HybridEngine implements Engine {

    @Override
    public String getType() { return "Hybrid"; }

    @Override
    public String start() { return "Hybrid engine started — switching between gas and electric"; }
}
