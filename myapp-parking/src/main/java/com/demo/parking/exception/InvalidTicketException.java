package com.demo.parking.exception;

public class InvalidTicketException extends RuntimeException {
    public InvalidTicketException(String ticketId) {
        super("Ticket not found: " + ticketId);
    }
}
