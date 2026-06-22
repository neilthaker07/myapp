package com.demo.parking.config;

import com.demo.parking.factory.ParkingFloorFactory;
import com.demo.parking.model.ParkingLot;
import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

// Wires the ParkingLot singleton via Builder — Spring calls this once at startup.
// All injection happens here so ParkingLot itself stays a plain Java class.
@Configuration
public class ParkingLotConfig {

    @Bean
    public ParkingLot parkingLot(ParkingFloorFactory factory) {
        var floorConfig = defaultFloorConfig();
        return new ParkingLot.Builder()
                .name("Downtown Parking")
                .addFloor(factory.createFloor(1, floorConfig))
                .addFloor(factory.createFloor(2, floorConfig))
                .addFloor(factory.createFloor(3, floorConfig))
                .build();
    }

    // 3 floors × 35 spots each = 105 total spots
    private Map<SpotType, Map<SpotSize, Integer>> defaultFloorConfig() {
        Map<SpotType, Map<SpotSize, Integer>> config = new LinkedHashMap<>();
        config.put(SpotType.MOTORCYCLE, Map.of(SpotSize.S, 5));
        config.put(SpotType.COMPACT,    Map.of(SpotSize.M, 10));
        config.put(SpotType.ELECTRIC,   Map.of(SpotSize.M, 3));
        config.put(SpotType.GENERAL,    new LinkedHashMap<>(Map.of(SpotSize.M, 10, SpotSize.L, 5)));
        config.put(SpotType.DISABLED,   Map.of(SpotSize.L, 2));
        return config;
    }
}
