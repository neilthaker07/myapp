package com.demo.parking.model.vehicle;

import com.demo.parking.model.enums.VehicleType;

public class Motorcycle extends Vehicle {

    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }
}
