package com.demo.myapp.car;

public class GasEngine implements Engine {

    @Override
    public String getType() { return "Gas"; }

    @Override
    public String start() { return "Gas engine started — combustion roaring"; }
}
