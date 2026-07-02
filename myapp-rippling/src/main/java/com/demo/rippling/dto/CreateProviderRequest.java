package com.demo.rippling.dto;

import java.math.BigDecimal;

public record CreateProviderRequest(
        String name,
        Integer zip,
        Integer lateAppointments,
        BigDecimal clientRetention,
        Integer age
) {}
