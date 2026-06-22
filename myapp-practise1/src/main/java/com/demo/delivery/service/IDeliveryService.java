package com.demo.delivery.service;

import java.math.BigDecimal;

public interface IDeliveryService {
    void registerDriver(String driverId, BigDecimal hourlyRate);
    void recordDelivery(String driverId, long startTime, long endTime);
    BigDecimal getTotalCost(String driverId);
    BigDecimal payUpTo(long timestamp);
    BigDecimal getTotalUnpaid();
}
