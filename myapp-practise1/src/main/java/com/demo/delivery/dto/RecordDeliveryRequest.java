package com.demo.delivery.dto;

import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class RecordDeliveryRequest {
    @Positive long startTime;  // epoch seconds
    @Positive long endTime;    // epoch seconds, exclusive
}
