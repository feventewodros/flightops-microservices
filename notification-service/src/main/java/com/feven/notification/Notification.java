package com.feven.notification;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingRef;
    private Long flightId;
    private String message;
    private Instant createdAt = Instant.now();

    public Notification() { }

    public Notification(String bookingRef, Long flightId, String message) {
        this.bookingRef = bookingRef;
        this.flightId = flightId;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getBookingRef() { return bookingRef; }
    public Long getFlightId() { return flightId; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
