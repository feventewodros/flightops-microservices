package com.feven.flight;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlightControllerTest {

    @Autowired MockMvc mvc;
    @Autowired FlightRepository flights;

    private Long fullFlightId() {
        return flights.findAll().stream()
                .filter(f -> "UA1204".equals(f.getFlightNumber()))
                .findFirst().orElseThrow().getId();  // seeded with 2 seats
    }

    @Test
    void searchReturnsSeededFlights() throws Exception {
        mvc.perform(get("/api/flights"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].flightNumber").exists());
    }

    @Test
    void reserveDecrementsSeats() throws Exception {
        Long id = fullFlightId();
        int before = flights.findById(id).orElseThrow().getAvailableSeats();
        mvc.perform(post("/api/flights/{id}/reserve", id))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.availableSeats").value(before - 1));
    }

    @Test
    void reserveReturns409WhenFull() throws Exception {
        Long id = fullFlightId();               // 2 seats
        mvc.perform(post("/api/flights/{id}/reserve", id)).andExpect(status().isOk());
        mvc.perform(post("/api/flights/{id}/reserve", id)).andExpect(status().isOk());
        mvc.perform(post("/api/flights/{id}/reserve", id)).andExpect(status().isConflict());
    }

    @Test
    void reserveReturns404WhenMissing() throws Exception {
        mvc.perform(post("/api/flights/{id}/reserve", 999999))
           .andExpect(status().isNotFound());
    }
}
