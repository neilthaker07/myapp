package com.demo.rippling.dto;

import java.math.BigDecimal;

public record ProviderResponse(
        Long providerId,
        String name,
        Integer zip,
        Integer age,
        Integer lateAppointments,
        BigDecimal clientRetention
) {}
