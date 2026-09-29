package com.feven.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookings;
    private final RestClient rest;
    private final String flightUrl;
    private final String notificationUrl;

    public BookingService(BookingRepository bookings, RestClient rest,
                          @Value("${services.flight-url}") String flightUrl,
                          @Value("${services.notification-url}") String notificationUrl) {
        this.bookings = bookings;
        this.rest = rest;
        this.flightUrl = flightUrl;
        this.notificationUrl = notificationUrl;
    }

    public Booking book(Long flightId, String passengerName, String seatNumber) {
        // 1. Reserve a seat on the flight service (service-to-service call)
        reserveSeat(flightId);

        // 2. Persist the booking locally
        String ref = "FLT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Booking booking = bookings.save(new Booking(ref, flightId, passengerName, seatNumber));

        // 3. Emit a notification (best-effort — a notification failure must not fail the booking)
        notify(booking);

        return booking;
    }

    private void reserveSeat(Long flightId) {
        try {
            rest.post()
                .uri(flightUrl + "/api/flights/{id}/reserve", flightId)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientResponseException e) {
            int code = e.getStatusCode().value();
            if (code == 404) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,
                        "Flight " + flightId + " not found");
            }
            if (code == 409) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,
                        "Flight " + flightId + " is full");
            }
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_GATEWAY,
                    "Flight service error: " + e.getStatusText());
        }
    }

    private void notify(Booking booking) {
        try {
            rest.post()
                .uri(notificationUrl + "/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "bookingRef", booking.getBookingRef(),
                        "flightId", booking.getFlightId(),
                        "message", "Booking confirmed for " + booking.getPassengerName()))
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Notification service unavailable, booking {} still confirmed: {}",
                    booking.getBookingRef(), e.getMessage());
        }
    }

    public Booking get(String ref) {
        return bookings.findByBookingRef(ref)
                .orElseThrow(() -> new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Booking not found: " + ref));
    }
}
