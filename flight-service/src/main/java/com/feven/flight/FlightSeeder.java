package com.feven.flight;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FlightSeeder {

    @Bean
    CommandLineRunner seedFlights(FlightRepository flights) {
        return args -> {
            if (flights.count() > 0) return;
            flights.saveAll(List.of(
                    new Flight("UA1201", "DEN", "LAX", "2026-10-01T08:00", "SCHEDULED", 160),
                    new Flight("UA1202", "DEN", "JFK", "2026-10-01T09:00", "SCHEDULED", 150),
                    new Flight("UA1203", "LAX", "DEN", "2026-10-01T14:00", "SCHEDULED", 160),
                    new Flight("UA1204", "DEN", "ORD", "2026-10-01T11:00", "SCHEDULED", 2)
            ));
        };
    }
}
