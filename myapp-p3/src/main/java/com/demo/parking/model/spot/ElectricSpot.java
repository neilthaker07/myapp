package com.demo.parking.model.spot;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.vehicle.ElectricCar;
import com.demo.parking.model.vehicle.Vehicle;

// Charging-enabled spot — only electric cars may use it.
public class ElectricSpot extends ParkingSpot {

    public ElectricSpot(String spotId, int floorNumber, int spotNumber) {
        super(spotId, floorNumber, spotNumber, SpotSize.M, SpotType.ELECTRIC);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof ElectricCar;
    }
}
