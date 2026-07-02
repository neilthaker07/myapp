package com.demo.rippling.dto;

public record AppointmentResponse(
        Long appointmentId,
        Long providerId,
        Long clientId,
        String startTime,
        String endTime,
        String status
) {}
