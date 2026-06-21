package com.demo.parking.controller;

import com.demo.parking.dto.LotStatusResponse;
import com.demo.parking.dto.ParkVehicleRequest;
import com.demo.parking.dto.ParkingTicketResponse;
import com.demo.parking.facade.ParkingFacade;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingFacade parkingFacade;

    public ParkingController(ParkingFacade parkingFacade) {
        this.parkingFacade = parkingFacade;
    }

    // POST /api/parking/park  { "vehicleType": "CAR", "licensePlate": "ABC-123" }
    @PostMapping("/park")
    public ResponseEntity<ParkingTicketResponse> park(@Valid @RequestBody ParkVehicleRequest request) {
        return new ResponseEntity<>(
                parkingFacade.park(request.getVehicleType(), request.getLicensePlate()),
                HttpStatus.CREATED);
    }

    // DELETE /api/parking/exit/{ticketId}
    @DeleteMapping("/exit/{ticketId}")
    public ResponseEntity<Void> exit(@PathVariable String ticketId) {
        parkingFacade.exit(ticketId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // GET /api/parking/ticket/{ticketId}
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<ParkingTicketResponse> getTicket(@PathVariable String ticketId) {
        return ResponseEntity.ok(parkingFacade.getTicket(ticketId));
    }

    // GET /api/parking/status
    @GetMapping("/status")
    public ResponseEntity<LotStatusResponse> getStatus() {
        return ResponseEntity.ok(parkingFacade.getLotStatus());
    }
}
