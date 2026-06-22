package com.demo.delivery.controller;

import com.demo.delivery.dto.PaymentResponse;
import com.demo.delivery.dto.RecordDeliveryRequest;
import com.demo.delivery.dto.RegisterDriverRequest;
import com.demo.delivery.dto.TotalCostResponse;
import com.demo.delivery.dto.TotalUnpaidResponse;
import com.demo.delivery.facade.DeliveryFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryFacade deliveryFacade;

    @PostMapping("/drivers")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerDriver(@Valid @RequestBody RegisterDriverRequest request) {
        deliveryFacade.registerDriver(request.getDriverId(), request.getHourlyRate());
    }

    @PostMapping("/drivers/{driverId}/deliveries")
    @ResponseStatus(HttpStatus.CREATED)
    public void recordDelivery(@PathVariable String driverId,
                               @Valid @RequestBody RecordDeliveryRequest request) {
        deliveryFacade.recordDelivery(driverId, request.getStartTime(), request.getEndTime());
    }

    @GetMapping("/drivers/{driverId}/cost")
    public TotalCostResponse getTotalCost(@PathVariable String driverId) {
        return new TotalCostResponse(driverId, deliveryFacade.getTotalCost(driverId));
    }

    @PostMapping("/payments")
    public PaymentResponse payUpTo(@RequestParam long upTo) {
        return new PaymentResponse(upTo, deliveryFacade.payUpTo(upTo));
    }

    @GetMapping("/payments/unpaid")
    public TotalUnpaidResponse getTotalUnpaid() {
        return new TotalUnpaidResponse(deliveryFacade.getTotalUnpaid());
    }
}
