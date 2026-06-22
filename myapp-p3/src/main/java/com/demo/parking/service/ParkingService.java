package com.demo.parking.service;

import com.demo.parking.exception.InvalidTicketException;
import com.demo.parking.factory.VehicleFactory;
import com.demo.parking.model.ParkingLot;
import com.demo.parking.model.ParkingTicket;
import com.demo.parking.model.enums.VehicleType;
import com.demo.parking.model.spot.ParkingSpot;
import com.demo.parking.model.vehicle.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ParkingService implements IParkingService {

    private final ParkingLot parkingLot;
    private final VehicleFactory vehicleFactory;
    private final Map<String, ParkingTicket> activeTickets = new ConcurrentHashMap<>();

    public ParkingService(ParkingLot parkingLot, VehicleFactory vehicleFactory) {
        this.parkingLot = parkingLot;
        this.vehicleFactory = vehicleFactory;
    }

    @Override
    public ParkingTicket park(VehicleType vehicleType, String licensePlate) {
        Vehicle vehicle = vehicleFactory.createVehicle(vehicleType, licensePlate);
        ParkingSpot spot = parkingLot.parkVehicle(vehicle);
        ParkingTicket ticket = new ParkingTicket(UUID.randomUUID().toString(), vehicle, spot, LocalDateTime.now());
        activeTickets.put(ticket.ticketId(), ticket);
        log.info("Ticket {} issued for {} at spot {}", ticket.ticketId(), vehicle, spot.getSpotId());
        return ticket;
    }

    @Override
    public void exit(String ticketId) {
        ParkingTicket ticket = activeTickets.remove(ticketId);
        if (ticket == null) throw new InvalidTicketException(ticketId);
        parkingLot.releaseSpot(ticket.spot());
        log.info("Ticket {} closed — {} exited from spot {}", ticketId, ticket.vehicle(), ticket.spot().getSpotId());
    }

    @Override
    public ParkingTicket getTicket(String ticketId) {
        ParkingTicket ticket = activeTickets.get(ticketId);
        if (ticket == null) throw new InvalidTicketException(ticketId);
        return ticket;
    }
}
