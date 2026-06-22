package com.demo.parking.model.spot;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.vehicle.Car;
import com.demo.parking.model.vehicle.Motorcycle;
import com.demo.parking.model.vehicle.Vehicle;

// Reserved accessible spot — accepts car-sized vehicles and motorcycles.
public class DisabledSpot extends ParkingSpot {

    public DisabledSpot(String spotId, int floorNumber, int spotNumber) {
        super(spotId, floorNumber, spotNumber, SpotSize.M, SpotType.DISABLED);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Car || vehicle instanceof Motorcycle;
    }
}
