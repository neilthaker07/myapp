package com.demo.parking.model;

import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import com.demo.parking.model.spot.ParkingSpot;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ParkingFloor {

    private final int floorNumber;

    // Double-keyed map: SpotType → SpotSize → deque of AVAILABLE spots of that exact type+size.
    // Every spot in a given deque is guaranteed compatible — poll() is always O(1) with no rejection.
    private final Map<SpotType, Map<SpotSize, Deque<ParkingSpot>>> availableSpots;

    // All spots keyed by spotId — used for O(1) release lookup.
    private final Map<String, ParkingSpot> allSpots;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.allSpots = new HashMap<>();
        this.availableSpots = new EnumMap<>(SpotType.class);
        for (SpotType type : SpotType.values()) {
            Map<SpotSize, Deque<ParkingSpot>> sizeMap = new EnumMap<>(SpotSize.class);
            for (SpotSize size : SpotSize.values()) {
                sizeMap.put(size, new ArrayDeque<>());
            }
            availableSpots.put(type, sizeMap);
        }
    }

    public void addSpot(ParkingSpot spot) {
        allSpots.put(spot.getSpotId(), spot);
        if (spot.isAvailable()) {
            availableSpots.get(spot.getType()).get(spot.getSize()).offer(spot);
        }
    }

    // Transfer all available spots to the lot-level pool — called once at ParkingLot init.
    // After this, the floor-level deques are empty; the lot owns the pool.
    public void drainAvailableSpotsTo(Map<SpotType, Map<SpotSize, Deque<ParkingSpot>>> target) {
        availableSpots.forEach((type, sizeMap) ->
                sizeMap.forEach((size, deque) -> {
                    target.get(type).get(size).addAll(deque);
                    deque.clear();
                }));
    }

    // --- Status helpers ---

    public int getTotalSpots() { return allSpots.size(); }

    public int getFloorNumber() { return floorNumber; }

    // --- Builder (demonstrates the pattern; used by ParkingFloorFactory) ---
    public static class Builder {

        private final int floorNumber;
        private final ParkingFloor floor;
        private int spotCounter = 1;

        public Builder(int floorNumber) {
            this.floorNumber = floorNumber;
            this.floor = new ParkingFloor(floorNumber);
        }

        public Builder addSpots(SpotType type, SpotSize size, int count) {
            for (int i = 0; i < count; i++) {
                String spotId = String.format("F%d-%03d", floorNumber, spotCounter++);
                ParkingSpot spot = createSpot(spotId, type, size);
                floor.addSpot(spot);
            }
            return this;
        }

        private ParkingSpot createSpot(String spotId, SpotType type, SpotSize size) {
            return switch (type) {
                case MOTORCYCLE -> new com.demo.parking.model.spot.MotorcycleSpot(spotId, floorNumber, spotCounter);
                case COMPACT    -> new com.demo.parking.model.spot.CompactSpot(spotId, floorNumber, spotCounter);
                case ELECTRIC   -> new com.demo.parking.model.spot.ElectricSpot(spotId, floorNumber, spotCounter);
                case GENERAL    -> new com.demo.parking.model.spot.GeneralSpot(spotId, floorNumber, spotCounter, size);
                case DISABLED   -> new com.demo.parking.model.spot.DisabledSpot(spotId, floorNumber, spotCounter);
            };
        }

        public ParkingFloor build() { return floor; }
    }
}
