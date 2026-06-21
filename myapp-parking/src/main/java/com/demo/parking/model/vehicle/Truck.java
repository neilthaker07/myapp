package com.demo.parking.model.vehicle;

import com.demo.parking.model.enums.VehicleType;

// Requires a GENERAL spot of size L — cannot fit in MOTORCYCLE, COMPACT, or ELECTRIC spots.
public class Truck extends Vehicle {

    public Truck(String licensePlate) {
        super(licensePlate, VehicleType.TRUCK);
    }
}
