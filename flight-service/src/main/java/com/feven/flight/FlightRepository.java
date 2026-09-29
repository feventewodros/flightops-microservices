package com.feven.flight;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByOriginIgnoreCaseAndDestinationIgnoreCase(String origin, String destination);
    List<Flight> findByOriginIgnoreCase(String origin);
    List<Flight> findByDestinationIgnoreCase(String destination);
}
