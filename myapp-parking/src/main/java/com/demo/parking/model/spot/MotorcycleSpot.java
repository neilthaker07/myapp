package com.demo.parking.model.spot;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.vehicle.Motorcycle;
import com.demo.parking.model.vehicle.Vehicle;

public class MotorcycleSpot extends ParkingSpot {

    public MotorcycleSpot(String spotId, int floorNumber, int spotNumber) {
        super(spotId, floorNumber, spotNumber, SpotSize.S, SpotType.MOTORCYCLE);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Motorcycle;
    }
}
