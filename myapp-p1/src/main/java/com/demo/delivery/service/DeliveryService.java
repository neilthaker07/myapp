package com.demo.delivery.service;

import com.demo.delivery.exception.DriverAlreadyExistsException;
import com.demo.delivery.exception.DriverNotFoundException;
import com.demo.delivery.model.DeliveryRecord;
import com.demo.delivery.model.Driver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DeliveryService implements IDeliveryService {

    private static final BigDecimal SECONDS_PER_HOUR = BigDecimal.valueOf(3600);

    private final ConcurrentHashMap<String, Driver> drivers = new ConcurrentHashMap<>();

    @Override
    public void registerDriver(String driverId, BigDecimal hourlyRate) {
        Driver existing = drivers.putIfAbsent(driverId, new Driver(driverId, hourlyRate));
        if (existing != null) {
            throw new DriverAlreadyExistsException(driverId);
        }
        log.info("Registered driver {} at rate {}/hr", driverId, hourlyRate);
    }

    @Override
    public void recordDelivery(String driverId, long startTime, long endTime) {
        Driver driver = findDriver(driverId);

        BigDecimal durationSeconds = BigDecimal.valueOf(endTime - startTime);
        // 1 hr rate is 15 then 2 hr charge = 2*15/1
        // Just converted into seconds
        BigDecimal cost = driver.getHourlyRate()
                .multiply(durationSeconds)
                .divide(SECONDS_PER_HOUR, 2, RoundingMode.HALF_UP);

        driver.addDelivery(new DeliveryRecord(startTime, endTime, cost));
        log.info("Recorded delivery for driver {}: start={}, end={}, cost={}", driverId, startTime, endTime, cost);
    }

    @Override
    public BigDecimal getTotalCost(String driverId) {
        return findDriver(driverId).getDeliveries().stream()
                .map(DeliveryRecord::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal payUpTo(long timestamp) {
        BigDecimal totalPaid = drivers.values().stream()
                .flatMap(driver -> {
                    long oldWatermark = driver.getPaidUpTo()
                            .getAndUpdate(current -> Math.max(current, timestamp));
                    return driver.getDeliveries().stream()
                            .map(record -> newlyPaidCost(driver, record, oldWatermark, timestamp));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Paid {} up to timestamp {}", totalPaid, timestamp);
        return totalPaid;
    }

    @Override
    public BigDecimal getTotalUnpaid() {
        return drivers.values().stream()
                .flatMap(driver -> driver.getDeliveries().stream()
                        .map(record -> unpaidCost(driver, record)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal newlyPaidCost(Driver driver, DeliveryRecord record, long oldWatermark, long newTimestamp) {
        long paidStart = Math.max(record.getStartTime(), oldWatermark);
        long paidEnd   = Math.min(record.getEndTime(), newTimestamp);
        return costForSlice(driver, paidStart, paidEnd);
    }

    private BigDecimal unpaidCost(Driver driver, DeliveryRecord record) {
        long unpaidStart = Math.max(record.getStartTime(), driver.getPaidUpTo().get());
        return costForSlice(driver, unpaidStart, record.getEndTime());
    }

    private BigDecimal costForSlice(Driver driver, long sliceStart, long sliceEnd) {
        if (sliceStart >= sliceEnd) return BigDecimal.ZERO;
        return driver.getHourlyRate()
                .multiply(BigDecimal.valueOf(sliceEnd - sliceStart))
                .divide(SECONDS_PER_HOUR, 2, RoundingMode.HALF_UP);
    }

    private Driver findDriver(String driverId) {
        Driver driver = drivers.get(driverId);
        if (driver == null) {
            throw new DriverNotFoundException(driverId);
        }
        return driver;
    }
}
