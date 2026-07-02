package com.demo.rippling.dto;

import java.util.List;

public record AvailabilityResponse(
        Long providerId,
        String date,
        List<String> availableSlots
) {}
