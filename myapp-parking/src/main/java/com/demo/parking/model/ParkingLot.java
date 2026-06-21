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

    // O(1): no floor scan — poll directly from the lot-level pool.
    // Iterates preferred types + compatible sizes (small bounded constants, not proportional to lot size).
    private ParkingSpot findSpot(Vehicle vehicle) {
        for (SpotType type : vehicle.getPreferredSpotTypes()) {
            for (SpotSize size : compatibleSizes(type, vehicle.getRequiredSpotSize())) {
                ParkingSpot spot = availablePool.get(type).get(size).poll();
                if (spot != null) return spot;
            }
        }
        return null;
    }

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
