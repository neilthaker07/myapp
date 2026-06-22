package com.demo.parking.model.spot;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.vehicle.Car;
import com.demo.parking.model.vehicle.Motorcycle;
import com.demo.parking.model.vehicle.Vehicle;

// Accepts Car (includes ElectricCar) and Motorcycle — not Truck.
// Car instanceof check covers ElectricCar since ElectricCar extends Car.
public class CompactSpot extends ParkingSpot {

    public CompactSpot(String spotId, int floorNumber, int spotNumber) {
        super(spotId, floorNumber, spotNumber, SpotSize.M, SpotType.COMPACT);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Car || vehicle instanceof Motorcycle;
    }
}
