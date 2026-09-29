package com.feven.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    public record BookingRequest(Long flightId, String passengerName, String seatNumber) { }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking book(@RequestBody BookingRequest req) {
        return service.book(req.flightId(), req.passengerName(), req.seatNumber());
    }

    @GetMapping("/{ref}")
    public Booking get(@PathVariable String ref) {
        return service.get(ref);
    }
}
