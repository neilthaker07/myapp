package com.demo.parking.facade;

import com.demo.parking.dto.LotStatusResponse;
import com.demo.parking.dto.ParkingMapper;
import com.demo.parking.dto.ParkingTicketResponse;
import com.demo.parking.model.ParkingLot;
import com.demo.parking.model.enums.VehicleType;
import com.demo.parking.service.IParkingService;
import org.springframework.stereotype.Service;

@Service
public class ParkingFacade {

    private final IParkingService parkingService;
    private final ParkingLot parkingLot;

    public ParkingFacade(IParkingService parkingService, ParkingLot parkingLot) {
        this.parkingService = parkingService;
        this.parkingLot = parkingLot;
    }

    public ParkingTicketResponse park(VehicleType type, String licensePlate) {
        return ParkingMapper.toTicketResponse(parkingService.park(type, licensePlate));
    }

    public void exit(String ticketId) {
        parkingService.exit(ticketId);
    }

    public ParkingTicketResponse getTicket(String ticketId) {
        return ParkingMapper.toTicketResponse(parkingService.getTicket(ticketId));
    }

    public LotStatusResponse getLotStatus() {
        return ParkingMapper.toLotStatus(parkingLot);
    }
}
