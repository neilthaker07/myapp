package com.demo.parking.model.enums;

import java.util.List;

// Each vehicle type carries its required spot size and preferred spot type priority order.
// TypeMatchStrategy walks preferredSpotTypes in order — most specific type first, GENERAL last.
public enum VehicleType {
    MOTORCYCLE(SpotSize.S,  List.of(SpotType.MOTORCYCLE, SpotType.GENERAL)),
    CAR       (SpotSize.M,  List.of(SpotType.COMPACT, SpotType.GENERAL, SpotType.DISABLED)),
    ELECTRIC_CAR(SpotSize.M, List.of(SpotType.ELECTRIC, SpotType.COMPACT, SpotType.GENERAL, SpotType.DISABLED)),
    TRUCK     (SpotSize.L,  List.of(SpotType.GENERAL, SpotType.DISABLED));

    private final SpotSize requiredSize;
    private final List<SpotType> preferredSpotTypes;

    VehicleType(SpotSize requiredSize, List<SpotType> preferredSpotTypes) {
        this.requiredSize = requiredSize;
        this.preferredSpotTypes = preferredSpotTypes;
    }

    public SpotSize getRequiredSize() { return requiredSize; }
    public List<SpotType> getPreferredSpotTypes() { return preferredSpotTypes; }
}
