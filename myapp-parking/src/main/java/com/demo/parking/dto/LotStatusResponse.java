package com.demo.parking.dto;

import java.util.List;

public record LotStatusResponse(
        String lotName,
        int totalFloors,
        int totalSpots,
        int availableSpots,
        List<FloorStatusResponse> floors
) {}
