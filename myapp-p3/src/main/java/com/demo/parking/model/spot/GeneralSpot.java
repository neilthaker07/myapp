package com.demo.parking.model.spot;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.vehicle.Vehicle;

// General-purpose spot — accepts any vehicle whose required size fits within this spot's size.
// The only spot type that comes in all four sizes (S, M, L).
public class GeneralSpot extends ParkingSpot {

    public GeneralSpot(String spotId, int floorNumber, int spotNumber, SpotSize size) {
        super(spotId, floorNumber, spotNumber, size, SpotType.GENERAL);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        // SpotSize ordinal encodes hierarchy: S=0, M=1, L=2
        return vehicle.getRequiredSpotSize().ordinal() <= getSize().ordinal();
    }
}
