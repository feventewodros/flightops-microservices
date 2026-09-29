package com.feven.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationRepository notifications;

    public NotificationController(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    public record NotificationRequest(String bookingRef, Long flightId, String message) { }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification receive(@RequestBody NotificationRequest req) {
        Notification saved = notifications.save(
                new Notification(req.bookingRef(), req.flightId(), req.message()));
        log.info("📨 Notification stored for booking {} (flight {})", req.bookingRef(), req.flightId());
        return saved;
    }

    @GetMapping
    public List<Notification> all() {
        return notifications.findAll();
    }
}
