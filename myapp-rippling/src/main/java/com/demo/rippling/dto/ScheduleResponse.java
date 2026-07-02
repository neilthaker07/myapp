package com.demo.rippling.dto;

public record ScheduleResponse(
        Long scheduleId,
        Long providerId,
        String dayOfWeek,
        String startTime,
        String endTime
) {}
