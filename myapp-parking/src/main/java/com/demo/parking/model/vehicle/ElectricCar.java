package com.demo.parking.model.vehicle;

import com.demo.parking.model.enums.VehicleType;

// Extends Car: same physical size (M), but prefers ELECTRIC spots for charging.
// instanceof Car is true — compact and general spots also accept it as a fallback.
public class ElectricCar extends Car {

    public ElectricCar(String licensePlate) {
        super(licensePlate, VehicleType.ELECTRIC_CAR);
    }
}
