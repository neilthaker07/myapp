package com.demo.parking.model.vehicle;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.enums.VehicleType;

import java.util.List;

public abstract class Vehicle {

    private final String licensePlate;
    private final VehicleType vehicleType;

    protected Vehicle(String licensePlate, VehicleType vehicleType) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getVehicleType() { return vehicleType; }

    // Delegates to the enum so subclasses don't need to repeat it
    public SpotSize getRequiredSpotSize() { return vehicleType.getRequiredSize(); }
    public List<SpotType> getPreferredSpotTypes() { return vehicleType.getPreferredSpotTypes(); }

    @Override
    public String toString() {
        return vehicleType + "(" + licensePlate + ")";
    }
}
