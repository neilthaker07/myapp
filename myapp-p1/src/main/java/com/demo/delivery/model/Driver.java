package com.demo.delivery.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class Driver {

    private final String driverId;
    private final BigDecimal hourlyRate;
    private final List<DeliveryRecord> deliveries = new CopyOnWriteArrayList<>();
    private final AtomicLong paidUpTo = new AtomicLong(0);

    public Driver(String driverId, BigDecimal hourlyRate) {
        this.driverId = driverId;
        this.hourlyRate = hourlyRate;
    }

    public void addDelivery(DeliveryRecord record) {
        deliveries.add(record);
    }

}
