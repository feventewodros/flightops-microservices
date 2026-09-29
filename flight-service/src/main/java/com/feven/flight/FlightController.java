package com.feven.flight;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Public flight search + an internal seat-reservation endpoint used by the booking service. */
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightRepository flights;

    public FlightController(FlightRepository flights) {
        this.flights = flights;
    }

    @GetMapping
    public List<Flight> search(@RequestParam(required = false) String origin,
                               @RequestParam(required = false) String destination) {
        boolean hasO = StringUtils.hasText(origin);
        boolean hasD = StringUtils.hasText(destination);
        if (hasO && hasD) return flights.findByOriginIgnoreCaseAndDestinationIgnoreCase(origin, destination);
        if (hasO) return flights.findByOriginIgnoreCase(origin);
        if (hasD) return flights.findByDestinationIgnoreCase(destination);
        return flights.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> get(@PathVariable Long id) {
        return flights.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Reserve one seat (decrement inventory). Called service-to-service by the booking service.
     * Returns 409 Conflict when the flight is full, 404 when it does not exist.
     */
    @PostMapping("/{id}/reserve")
    public ResponseEntity<?> reserve(@PathVariable Long id) {
        return flights.findById(id).map(flight -> {
            if (flight.getAvailableSeats() <= 0) {
                return ResponseEntity.status(409).body("Flight is full");
            }
            flight.setAvailableSeats(flight.getAvailableSeats() - 1);
            flights.save(flight);
            return ResponseEntity.ok(flight);
        }).orElse(ResponseEntity.notFound().build());
    }
}
