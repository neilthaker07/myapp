package com.demo.parking.state;

import com.demo.parking.model.enums.SpotStatus;
import com.demo.parking.model.spot.ParkingSpot;
import com.demo.parking.model.vehicle.Vehicle;

public class OutOfServiceState implements ParkingSpotState {

    @Override
    public boolean isAvailable() { return false; }

    @Override
    public void park(ParkingSpot spot, Vehicle vehicle) {
        throw new IllegalStateException("Spot " + spot.getSpotId() + " is out of service");
    }

    @Override
    public void release(ParkingSpot spot) {
        throw new IllegalStateException("Spot " + spot.getSpotId() + " is out of service");
    }

    @Override
    public SpotStatus getStatus() { return SpotStatus.OUT_OF_SERVICE; }
}
