package com.demo.delivery.exception;

public class DriverAlreadyExistsException extends RuntimeException {
    public DriverAlreadyExistsException(String driverId) {
        super("Driver already registered: " + driverId);
    }
}
