package com.demo.parking.factory;

import com.demo.parking.model.enums.VehicleType;
import com.demo.parking.model.vehicle.Car;
import com.demo.parking.model.vehicle.ElectricCar;
import com.demo.parking.model.vehicle.Motorcycle;
import com.demo.parking.model.vehicle.Truck;
import com.demo.parking.model.vehicle.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleFactory {

    public Vehicle createVehicle(VehicleType type, String licensePlate) {
        return switch (type) {
            case MOTORCYCLE  -> new Motorcycle(licensePlate);
            case CAR         -> new Car(licensePlate);
            case ELECTRIC_CAR -> new ElectricCar(licensePlate);
            case TRUCK       -> new Truck(licensePlate);
        };
    }
}
