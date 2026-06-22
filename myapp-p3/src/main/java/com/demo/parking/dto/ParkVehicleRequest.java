package com.demo.parking.dto;

import com.demo.parking.model.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ParkVehicleRequest {

    @NotNull(message = "vehicleType is required")
    private VehicleType vehicleType;

    @NotBlank(message = "licensePlate is required")
    @Size(max = 20, message = "licensePlate must not exceed 20 characters")
    private String licensePlate;

    public ParkVehicleRequest() {}

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
}
