package com.demo.parking.dto;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.enums.VehicleType;

public record ParkingTicketResponse(
        String ticketId,
        String licensePlate,
        VehicleType vehicleType,
        String spotId,
        int floorNumber,
        SpotType spotType,
        SpotSize spotSize,
        String entryTime
) {}
