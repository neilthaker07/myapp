package com.demo.parking.state;

import com.demo.parking.model.enums.SpotStatus;
import com.demo.parking.model.spot.ParkingSpot;
import com.demo.parking.model.vehicle.Vehicle;

// State pattern — each concrete state encodes what is legal in that state.
// ParkingSpot is the context; it delegates park() and release() here.
public interface ParkingSpotState {
    boolean isAvailable();
    void park(ParkingSpot spot, Vehicle vehicle);
    void release(ParkingSpot spot);
    SpotStatus getStatus();
}
