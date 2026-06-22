package com.demo.parking.model.enums;

// Ordinal encodes size hierarchy: S=0 < M=1 < L=2
// Used for size-compatibility checks: vehicle.requiredSize.ordinal() <= spot.size.ordinal()
public enum SpotSize {
    S, M, L
}
