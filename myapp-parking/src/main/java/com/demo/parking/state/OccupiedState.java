package com.demo.parking.state;

import com.demo.parking.model.enums.SpotStatus;
import com.demo.parking.model.spot.ParkingSpot;
import com.demo.parking.model.vehicle.Vehicle;

public class OccupiedState implements ParkingSpotState {

    @Override
    public boolean isAvailable() { return false; }

    @Override
    public void park(ParkingSpot spot, Vehicle vehicle) {
        throw new IllegalStateException("Spot " + spot.getSpotId() + " is already occupied");
    }

    @Override
    public void release(ParkingSpot spot) {
        spot.setParkedVehicle(null);
        spot.setState(new AvailableState());
    }

    @Override
    public SpotStatus getStatus() { return SpotStatus.OCCUPIED; }
}
