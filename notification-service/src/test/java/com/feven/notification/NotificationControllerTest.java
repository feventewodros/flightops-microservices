package com.feven.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificationControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void storesAndListsNotifications() throws Exception {
        String body = """
            {"bookingRef":"FLT-ABC123","flightId":1,"message":"Booking confirmed for Feven"}
            """;
        mvc.perform(post("/api/notifications").contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.bookingRef").value("FLT-ABC123"))
           .andExpect(jsonPath("$.id").exists());

        mvc.perform(get("/api/notifications"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].message").value("Booking confirmed for Feven"));
    }
}
