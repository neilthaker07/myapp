package com.demo.parking.dto;

public record FloorStatusResponse(
        int floorNumber,
        int totalSpots
) {}
