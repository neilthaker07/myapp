package com.demo.parking.dto;

import com.demo.parking.model.ParkingFloor;
import com.demo.parking.model.ParkingLot;
import com.demo.parking.model.ParkingTicket;

import java.util.List;

public class ParkingMapper {

    private ParkingMapper() {}

    public static ParkingTicketResponse toTicketResponse(ParkingTicket ticket) {
        return new ParkingTicketResponse(
                ticket.ticketId(),
                ticket.vehicle().getLicensePlate(),
                ticket.vehicle().getVehicleType(),
                ticket.spot().getSpotId(),
                ticket.spot().getFloorNumber(),
                ticket.spot().getType(),
                ticket.spot().getSize(),
                ticket.entryTime().toString()
        );
    }

    public static LotStatusResponse toLotStatus(ParkingLot lot) {
        List<FloorStatusResponse> floorResponses = lot.getFloors().stream()
                .map(ParkingMapper::toFloorStatus)
                .toList();
        int total = floorResponses.stream().mapToInt(FloorStatusResponse::totalSpots).sum();
        return new LotStatusResponse(lot.getName(), lot.getFloors().size(), total, lot.getAvailableCount(), floorResponses);
    }

    private static FloorStatusResponse toFloorStatus(ParkingFloor floor) {
        return new FloorStatusResponse(floor.getFloorNumber(), floor.getTotalSpots());
    }
}
