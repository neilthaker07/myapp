package com.demo.parking.model;

import com.demo.parking.model.spot.ParkingSpot;
import com.demo.parking.model.vehicle.Vehicle;

import java.time.LocalDateTime;

public record ParkingTicket(
        String ticketId,
        Vehicle vehicle,
        ParkingSpot spot,
        LocalDateTime entryTime
) {}
