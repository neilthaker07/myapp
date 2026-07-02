package com.demo.rippling.dto;

// date: "yyyy-MM-dd" e.g. "2024-06-03" | startTime: "HH:mm" e.g. "09:00"
public record BookAppointmentRequest(
        Long providerId,
        Long clientId,
        String date,
        String startTime
) {}
