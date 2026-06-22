package com.demo.parking.state;

import com.demo.parking.model.enums.SpotStatus;
import com.demo.parking.model.spot.ParkingSpot;
import com.demo.parking.model.vehicle.Vehicle;

public class AvailableState implements ParkingSpotState {

    @Override
    public boolean isAvailable() { return true; }

    @Override
    public void park(ParkingSpot spot, Vehicle vehicle) {
        spot.setParkedVehicle(vehicle);
        spot.setState(new OccupiedState());
    }

    @Override
    public void release(ParkingSpot spot) {
        throw new IllegalStateException("Spot " + spot.getSpotId() + " is already available");
    }

    @Override
    public SpotStatus getStatus() { return SpotStatus.AVAILABLE; }
}
