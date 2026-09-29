package com.feven.booking;

import jakarta.persistence.*;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingRef;
    private Long flightId;
    private String passengerName;
    private String seatNumber;

    public Booking() { }

    public Booking(String bookingRef, Long flightId, String passengerName, String seatNumber) {
        this.bookingRef = bookingRef;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.seatNumber = seatNumber;
    }

    public Long getId() { return id; }
    public String getBookingRef() { return bookingRef; }
    public Long getFlightId() { return flightId; }
    public String getPassengerName() { return passengerName; }
    public String getSeatNumber() { return seatNumber; }
}
