package com.feven.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired MockMvc mvc;

    @MockBean BookingService service;

    @Test
    void bookReturns201AndBooking() throws Exception {
        when(service.book(eq(1L), any(), any()))
                .thenReturn(new Booking("FLT-XYZ789", 1L, "Feven", "1A"));

        String body = """
            {"flightId":1,"passengerName":"Feven","seatNumber":"1A"}
            """;
        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.bookingRef").value("FLT-XYZ789"))
           .andExpect(jsonPath("$.passengerName").value("Feven"));
    }
}
