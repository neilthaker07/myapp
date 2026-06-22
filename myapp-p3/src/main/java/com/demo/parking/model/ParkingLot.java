package com.demo.parking.model;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.spot.ParkingSpot;
import com.demo.parking.model.vehicle.Vehicle;
import com.demo.parking.exception.NoAvailableSpotException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// Singleton pattern — one parking lot per JVM.
// Spring manages the instance as a @Bean (declared in ParkingLotConfig);
// the static getInstance() demonstrates the classical double-checked locking pattern.
@Slf4j
public class ParkingLot {

    private static volatile ParkingLot instance;

    private final String name;
    private final List<ParkingFloor> floors;
    private final Map<SpotType, Map<SpotSize, Deque<ParkingSpot>>> availablePool;

    private ParkingLot(String name, List<ParkingFloor> floors) {
        this.name = name;
        this.floors = new ArrayList<>(floors);
        this.availablePool = new EnumMap<>(SpotType.class);
        for (SpotType type : SpotType.values()) {
            Map<SpotSize, Deque<ParkingSpot>> sizeMap = new EnumMap<>(SpotSize.class);
            for (SpotSize size : SpotSize.values()) {
                sizeMap.put(size, new ArrayDeque<>());
            }
            availablePool.put(type, sizeMap);
        }
        for (ParkingFloor floor : this.floors) {
            floor.drainAvailableSpotsTo(availablePool);
        }
        instance = this;
        log.info("ParkingLot '{}' initialized: {} floors, {} total spots",
                name, floors.size(), floors.stream().mapToInt(ParkingFloor::getTotalSpots).sum());
    }

    // Classical Singleton accessor — used in non-Spring contexts (tests, standalone demos).
    // In production code, inject ParkingLot via Spring DI instead of calling this.
    public static ParkingLot getInstance() {
        return Objects.requireNonNull(instance, "ParkingLot not yet initialized");
    }

    public ParkingSpot parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findSpot(vehicle);
        if (spot == null) {
            throw new NoAvailableSpotException(
                    "No available spot for " + vehicle.getVehicleType() + " (" + vehicle.getLicensePlate() + ")");
        }
        spot.park(vehicle);
        log.info("Parked {} at spot {} (floor {})", vehicle, spot.getSpotId(), spot.getFloorNumber());
        return spot;
    }

    /**
     * Finds and claims the best available spot for the given vehicle in O(1) time.
     *
     * Step 1 — preferred types: VehicleType enum encodes a priority-ordered list of spot types
     *   e.g. ELECTRIC_CAR → [ELECTRIC, COMPACT, GENERAL]. We try ELECTRIC first to preserve
     *   charging spots for electric vehicles; GENERAL is always the last fallback.
     *
     * Step 2 — compatible sizes: for each candidate type, ask compatibleSizes() which sizes
     *   of that type this vehicle can legally use. We iterate those sizes and call poll() on
     *   the corresponding deque in availablePool.
     *
     * Step 3 — poll(): removes and returns the head of the deque in O(1). Returns null if the
     *   deque is empty (no spot of that type+size available), so we try the next combination.
     *
     * Total iterations ≤ |SpotType| × |SpotSize| = 5 × 3 = 15 — a fixed constant regardless
     * of how many floors or spots the lot has. This is why it is O(1).
     */
    private ParkingSpot findSpot(Vehicle vehicle) {
        for (SpotType type : vehicle.getPreferredSpotTypes()) {
            for (SpotSize size : compatibleSizes(type, vehicle.getRequiredSpotSize())) {
                ParkingSpot spot = availablePool.get(type).get(size).poll();
                if (spot != null) return spot;
            }
        }
        return null;
    }

    /**
     * Returns the spot sizes that are legal for a given type + vehicle's minimum required size.
     *
     * GENERAL spots come in multiple sizes (M, L) — a vehicle that needs size M can also fit
     * in an L spot, so we return all sizes >= minSize in ascending order (prefer exact fit first
     * to avoid wasting a large spot on a small vehicle).
     *   e.g. minSize=M → [M, L]  |  minSize=L → [L]
     *
     * All other spot types (MOTORCYCLE, COMPACT, ELECTRIC, DISABLED) have a single fixed size.
     * There is no benefit in trying a different size — the spot type itself enforces compatibility.
     * List.of(minSize) returns a single-element immutable list, e.g. [M] for COMPACT.
     */
    private List<SpotSize> compatibleSizes(SpotType type, SpotSize minSize) {
        if (type == SpotType.GENERAL) {
            return Arrays.stream(SpotSize.values())
                    .filter(s -> s.ordinal() >= minSize.ordinal())
                    .toList();
        }
        return List.of(minSize);
    }

    public void releaseSpot(ParkingSpot spot) {
        spot.release();
        availablePool.get(spot.getType()).get(spot.getSize()).offerFirst(spot);
        log.info("Released spot {} (floor {})", spot.getSpotId(), spot.getFloorNumber());
    }

    public int getAvailableCount() {
        return availablePool.values().stream()
                .flatMap(m -> m.values().stream())
                .mapToInt(Deque::size)
                .sum();
    }

    public List<ParkingFloor> getFloors() { return Collections.unmodifiableList(floors); }
    public String getName() { return name; }

    // --- Builder (used by ParkingLotConfig @Bean) ---
    public static class Builder {

        private String name = "Parking Lot";
        private final List<ParkingFloor> floors = new ArrayList<>();

        public Builder name(String name) { this.name = name; return this; }
        public Builder addFloor(ParkingFloor floor) { floors.add(floor); return this; }

        public ParkingLot build() {
            if (floors.isEmpty()) throw new IllegalStateException("Parking lot must have at least one floor");
            return new ParkingLot(name, floors);
        }
    }
}
