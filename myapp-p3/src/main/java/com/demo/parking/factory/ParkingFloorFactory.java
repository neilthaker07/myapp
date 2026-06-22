package com.demo.parking.factory;

import com.demo.parking.model.ParkingFloor;
import com.demo.parking.model.enums.SpotSize;
import com.demo.parking.model.enums.SpotType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ParkingFloorFactory {

    // Creates a floor using ParkingFloor.Builder.
    // config: SpotType → (SpotSize → count), e.g. GENERAL → {M: 10, L: 5}
    public ParkingFloor createFloor(int floorNumber, Map<SpotType, Map<SpotSize, Integer>> config) {
        ParkingFloor.Builder builder = new ParkingFloor.Builder(floorNumber);
        config.forEach((type, sizeMap) ->
                sizeMap.forEach((size, count) -> builder.addSpots(type, size, count)));
        return builder.build();
    }
}
