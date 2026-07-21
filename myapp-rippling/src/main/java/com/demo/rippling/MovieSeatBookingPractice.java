package com.demo.rippling;

import lombok.Getter;
import lombok.Setter;

import java.awt.print.Book;
import java.util.*;

/*
 * PROBLEM: Movie Seat Booking System
 *
 * Design and implement a small in-memory booking system for a cinema. This is
 * an object-design exercise, not a data-structure-trick exercise — the point is
 * how you split responsibility across classes, and where you put validation
 * and mutation, not clever algorithms.
 *
 * Domain:
 *   - A Show has an id, a movie name, and a fixed set of Seats (each with a
 *     seatId, e.g. "A1", "A2", "B1"...). Every seat starts AVAILABLE.
 *   - A Seat can be AVAILABLE or BOOKED. Nothing outside the class that owns
 *     a Seat's lifecycle should be able to flip its status directly.
 *   - A Booking represents one successful reservation: a bookingId, which
 *     show, which seatIds, and the customer's name.
 *   - A CinemaBookingSystem holds multiple Shows and is the only entry point
 *     callers use — they never talk to a Show or Seat directly.
 *
 * Implement, on CinemaBookingSystem:
 *
 *   1. bookSeats(showId, List<seatId>, customerName) -> Booking
 *        - Books ALL requested seats for that show, or NONE of them.
 *          If even one requested seat is already booked (or doesn't exist),
 *          the whole request must fail and leave every seat's state unchanged
 *          — no partial booking.
 *        - On success, mark all requested seats BOOKED and return a new
 *          Booking with a unique bookingId.
 *
 *   2. getAvailableSeatIds(showId) -> List<String>
 *        - Returns the seatIds currently AVAILABLE for that show.
 *
 *   3. cancelBooking(bookingId)
 *        - Releases every seat that booking held back to AVAILABLE.
 *        - Cancelling an unknown or already-cancelled bookingId should not
 *          silently succeed — decide what should happen and be able to
 *          justify it.
 *
 * Example walkthrough:
 *   Show "S1" ("Dune Part 3") has seats: A1, A2, A3, B1, B2 — all AVAILABLE.
 *
 *   bookSeats("S1", ["A1","A2"], "Alice")
 *     -> succeeds, returns booking "BK1"; A1 and A2 are now BOOKED.
 *
 *   bookSeats("S1", ["A2","B1"], "Bob")
 *     -> must FAIL (A2 already booked) — and B1 must remain AVAILABLE,
 *        i.e. Bob's request must not book B1 while rejecting A2.
 *
 *   getAvailableSeatIds("S1") -> [A3, B1, B2]
 *
 *   cancelBooking("BK1")
 *     -> A1 and A2 become AVAILABLE again.
 *
 *   getAvailableSeatIds("S1") -> [A1, A2, A3, B1, B2]
 *
 * Edge cases to think about:
 *   - Booking a seatId that doesn't exist on that show at all.
 *   - Booking the same seatId twice in one request (["A1","A1"]).
 *   - Booking against an unknown showId.
 *   - Cancelling a bookingId that was already cancelled, or never existed.
 *   - An empty seatId list passed to bookSeats — should that be allowed?
 *
 * Interview framing — be ready to justify these out loud:
 *   - Where does the "seat can't be double-booked" invariant actually live?
 *     Can a caller reach into a Show or Seat and corrupt state directly, the
 *     way getItems() used to leak a mutable map in the kiosk-order exercise?
 *   - Why does bookSeats need to be all-or-nothing rather than booking seats
 *     one at a time in a loop? What would go wrong with the loop version on
 *     the Bob example above?
 *   - If two customers called bookSeats for the same seat at the same instant
 *     from two threads, what breaks first, and where would you put a lock or
 *     use a concurrency-safe structure to fix it?
 *   - If this were a real Spring Boot service, which class(es) become
 *     @Entity, which becomes @Service, and what does the controller layer
 *     actually expose vs. keep hidden?
 */
public class MovieSeatBookingPractice {

    enum SeatStatus { AVAILABLE, BOOKED }

    // TODO: flesh out Seat — status should not be settable from outside this class's control.
    @Getter
    @Setter
    static class Seat {
        private final String seatId;
        private SeatStatus status = SeatStatus.AVAILABLE;

        Seat(String seatId) {
            this.seatId = seatId;
        }
    }

    // TODO: flesh out Show — owns its Seats, exposes only what callers legitimately need.
    @Getter
    static class Show {
        private final String showId;
        private final String movieName;
        private final Map<String, Seat> seatsById;

        Show(String showId, String movieName, List<String> seatIds) {
            this.showId = showId;
            this.movieName = movieName;
            this.seatsById = new java.util.LinkedHashMap<>();
            for (String seatId : seatIds) {
                seatsById.put(seatId, new Seat(seatId));
            }
        }
    }

    // TODO: flesh out Booking — a simple immutable record of what was booked.
    record Booking(String bookingId, String showId, List<String> seatIds, String customerName) {}

    // TODO: flesh out CinemaBookingSystem — the only class callers interact with.
    static class CinemaBookingSystem {
        private final Map<String, Show> showsById = new java.util.LinkedHashMap<>();
        private final Map<String, Booking> bookings = new HashMap<>();

        void addShow(Show show) {
            showsById.put(show.getShowId(), show);
        }

        Booking bookSeats(String showId, List<String> seatIds, String customerName) throws Exception {
            if (!showsById.containsKey(showId)) {
                throw new Exception("show doesn't exist");
            }
            Show show = showsById.get(showId);
            Map<String, Seat> availableSeats = show.getSeatsById();
            for (String seatId : seatIds) {
                if (!availableSeats.containsKey(seatId)) {
                    throw new Exception("Seat "+ seatId +" is already booked");
                }
            }
            // book seats
            for (Seat s : availableSeats.values()) {
                s.setStatus(SeatStatus.BOOKED);
            }
            // create a new booking
            Booking booking = new Booking(UUID.randomUUID().toString(), showId, seatIds, customerName);
            bookings.put(booking.bookingId, booking);
            return booking;
        }

        List<String> getAvailableSeatIds(String showId) {
            Show show = showsById.get(showId);
            Map<String, Seat> availableSeats = show.getSeatsById();
            return availableSeats.keySet().stream().toList();
        }

        void cancelBooking(String bookingId) {
            Booking b = bookings.get(bookingId);
            List<String> cancelledSeats = b.seatIds;
            String showId = b.showId;
            Show show = showsById.get(showId);
            Map<String, Seat> allSeats = show.getSeatsById();
            for (String seatId: allSeats.keySet()) {
                if (cancelledSeats.contains(seatId)) {
                    Seat ss = allSeats.get(seatId);
                    ss.setStatus(SeatStatus.AVAILABLE);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        CinemaBookingSystem system = new CinemaBookingSystem();
        Show show = new Show("S1", "Dune Part 3", List.of("A1", "A2", "A3", "B1", "B2"));
        system.addShow(show);

        // TODO: drive through the example walkthrough above and print results
        // at each step, including the expected failure for Bob's request.
//        String showId, List<String> seatIds, String customerName) {
        Booking b1 = system.bookSeats("S1", List.of("A1", "A2"), "Alice");
        Booking b2 = system.bookSeats("S1", List.of("A3", "B2"), "Bob");

        system.cancelBooking(b1.bookingId);
    }
}
