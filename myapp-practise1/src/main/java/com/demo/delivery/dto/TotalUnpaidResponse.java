package com.demo.delivery.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class TotalUnpaidResponse {
    BigDecimal totalUnpaid;
}
