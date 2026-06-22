package com.demo.delivery.model;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class DeliveryRecord {
    long startTime;   // epoch seconds, inclusive
    long endTime;     // epoch seconds, exclusive
    BigDecimal cost;  // pre-computed: hourlyRate * (duration / 3600)
}
