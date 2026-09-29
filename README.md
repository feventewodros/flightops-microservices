# ✈️ FlightOps Microservices

A **microservices** version of FlightOps: an **API gateway** fronting three independent Spring Boot
services, each with its own database, communicating over REST. Demonstrates service decomposition,
an API gateway, inter-service communication, and container orchestration with Docker Compose.

> Built by Feven Tewodros to show microservice architecture end to end — not a buzzword on a résumé.

---

## Architecture
```
                         ┌──────────────────────────┐
   client ──▶ :8080 ────▶│   API Gateway            │  (Spring Cloud Gateway)
                         │   routes by path prefix  │
                         └───┬───────────┬──────────┘
             /api/flights/** │           │ /api/bookings/**   /api/notifications/**
                             ▼           ▼                     ▼
                    ┌──────────────┐  ┌──────────────┐   ┌────────────────────┐
                    │ flight-svc   │  │ booking-svc  │   │ notification-svc   │
                    │  :8082 · H2  │  │  :8083 · H2  │   │  :8084 · H2        │
                    └──────▲───────┘  └──┬───────┬───┘   └─────────▲──────────┘
                           │ reserve seat│       │ emit notification │
                           └─────────────┘       └───────────────────┘
```

When a booking is created, **booking-service**:
1. calls **flight-service** to reserve a seat (`POST /api/flights/{id}/reserve`) — returns `409` if full,
2. persists the booking in its own database,
3. emits an event to **notification-service** (best-effort — a notification outage never fails a booking).

Each service owns its own H2 database — no shared schema. The **gateway** is the only public entry point.

## Modules
| Module | Port | Responsibility |
|---|---|---|
| `gateway` | 8080 | Single entry point; routes `/api/**` to the right service (Spring Cloud Gateway, reactive) |
| `flight-service` | 8082 | Flight data + seat inventory; public search + internal `reserve` |
| `booking-service` | 8083 | Orchestrates bookings across flight + notification services |
| `notification-service` | 8084 | Records booking notifications |

## Run everything (Docker Compose)
```bash
docker compose up --build
```
All four services start; the gateway is at **http://localhost:8080**.

## Run locally without Docker
From the repo root, start each module in its own terminal:
```bash
mvn -pl flight-service       spring-boot:run
mvn -pl notification-service spring-boot:run
mvn -pl booking-service      spring-boot:run
mvn -pl gateway              spring-boot:run
```

## Try the full flow (through the gateway)
```bash
# search flights (gateway → flight-service)
curl localhost:8080/api/flights

# book a seat (gateway → booking-service → flight-service reserve → notification-service)
curl -X POST localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"flightId":1,"passengerName":"Feven","seatNumber":"1A"}'

# see the notification that booking-service emitted (gateway → notification-service)
curl localhost:8080/api/notifications
```

## Tech stack
Java 17 · Spring Boot 3.2 · Spring Cloud Gateway 2023.0.1 · Spring Data JPA · H2 · Spring `RestClient` ·
multi-module Maven · Docker · Docker Compose · JUnit 5

## Run the tests
```bash
mvn test        # builds and tests all four modules
```

## License
MIT
