package com.demo.delivery.facade;

import com.demo.delivery.service.IDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DeliveryFacade {

    private final IDeliveryService deliveryService;

    public void registerDriver(String driverId, BigDecimal hourlyRate) {
        deliveryService.registerDriver(driverId, hourlyRate);
    }

    public void recordDelivery(String driverId, long startTime, long endTime) {
        if (endTime <= startTime) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        deliveryService.recordDelivery(driverId, startTime, endTime);
    }

    public BigDecimal getTotalCost(String driverId) {
        return deliveryService.getTotalCost(driverId);
    }

    public BigDecimal payUpTo(long timestamp) {
        return deliveryService.payUpTo(timestamp);
    }

    public BigDecimal getTotalUnpaid() {
        return deliveryService.getTotalUnpaid();
    }
}
