package com.demo.parking.exception;

public class NoAvailableSpotException extends RuntimeException {
    public NoAvailableSpotException(String message) { super(message); }
}
