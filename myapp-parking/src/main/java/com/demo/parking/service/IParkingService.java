package com.demo.parking.service;

import com.demo.parking.model.ParkingTicket;
import com.demo.parking.model.enums.VehicleType;

public interface IParkingService {
    ParkingTicket park(VehicleType vehicleType, String licensePlate);
    void exit(String ticketId);
    ParkingTicket getTicket(String ticketId);
}
