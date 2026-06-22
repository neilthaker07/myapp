package com.demo.parking.model.spot;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotStatus;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.vehicle.Vehicle;
import com.demo.parking.state.AvailableState;
import com.demo.parking.state.ParkingSpotState;

// Abstract context in the State pattern.
// Subclasses define canFitVehicle() — the polymorphic check for vehicle-spot compatibility.
public abstract class ParkingSpot {

    private final String spotId;      // e.g. "F1-003"
    private final int floorNumber;
    private final int spotNumber;
    private final SpotSize size;
    private final SpotType type;
    private ParkingSpotState state;
    private Vehicle parkedVehicle;

    protected ParkingSpot(String spotId, int floorNumber, int spotNumber, SpotSize size, SpotType type) {
        this.spotId = spotId;
        this.floorNumber = floorNumber;
        this.spotNumber = spotNumber;
        this.size = size;
        this.type = type;
        this.state = new AvailableState();
    }

    // --- Polymorphic contract — every subclass must implement ---
    public abstract boolean canFitVehicle(Vehicle vehicle);

    // --- State delegation ---
    public boolean isAvailable() { return state.isAvailable(); }
    public SpotStatus getStatus() { return state.getStatus(); }

    public void park(Vehicle vehicle) { state.park(this, vehicle); }
    public void release() { state.release(this); }

    // Called by state objects during transitions — kept public so state classes
    // in a separate package can reach them (same pattern as Book.setState in main app)
    public void setState(ParkingSpotState state) { this.state = state; }
    public void setParkedVehicle(Vehicle vehicle) { this.parkedVehicle = vehicle; }

    // --- Getters ---
    public String getSpotId() { return spotId; }
    public int getFloorNumber() { return floorNumber; }
    public int getSpotNumber() { return spotNumber; }
    public SpotSize getSize() { return size; }
    public SpotType getType() { return type; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }
}
