package com.demo.rippling.dto;

// dayOfWeek: "MONDAY" | startTime/endTime: "HH:mm" e.g. "09:00"
public record AddScheduleRequest(
        String dayOfWeek,
        String startTime,
        String endTime
) {}
