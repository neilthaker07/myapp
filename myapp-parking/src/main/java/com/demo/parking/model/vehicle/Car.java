package com.demo.parking.model.vehicle;

import com.demo.parking.model.enums.VehicleType;

public class Car extends Vehicle {

    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }

    // Protected constructor for ElectricCar to pass its own VehicleType
    protected Car(String licensePlate, VehicleType type) {
        super(licensePlate, type);
    }
}
