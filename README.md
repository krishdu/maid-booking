# Maid Booking Service - Krishnendu

A small Spring Boot backend to onboard maids, search them, book a slot (Instant or Scheduled), pay by UPI or card, and cancel.

## How to run

Requires Java 17+ and Maven.

```bash
mvn test              # run unit tests
mvn spring-boot:run   # start on http://localhost:8080
```

## API endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/maids` | Onboard a maid |
| GET | `/maids/{id}` | Get a maid |
| GET | `/maids?locality=&service=&minRating=&maxPrice=` | Search maids (all filters optional) |
| POST | `/bookings` | Book a maid |
| GET | `/bookings/{id}` | Get a booking |
| POST | `/bookings/{id}/payment` | Pay for a booking |
| POST | `/bookings/{id}/cancel` | Cancel a booking and free the slot |

Errors return `{"error": "..."}` with `400` (bad input), `404` (not found) or `409` (slot taken, invalid state).

### Sample requests

```bash
curl -X POST localhost:8080/maids -H 'Content-Type: application/json' -d '{
  "name":"Asha","locality":"Koramangala","services":["CLEANING","COOKING"],
  "pricePerHour":200,"rating":4.5,"workStart":"08:00","workEnd":"18:00"}'

curl 'localhost:8080/maids?locality=Koramangala&service=CLEANING&maxPrice=250'

curl -X POST localhost:8080/bookings -H 'Content-Type: application/json' -d '{
  "maidId":"<maidId>","type":"SCHEDULED","start":"2030-01-01T10:00:00","hours":2}'

curl -X POST localhost:8080/bookings/<bookingId>/payment -H 'Content-Type: application/json' -d '{
  "method":"UPI","details":"asha@okaxis"}'

curl -X POST localhost:8080/bookings/<bookingId>/cancel
```

For an `INSTANT` booking, omit `start`; the slot starts now.

### Postman

Import `maid-booking.postman_collection.json` and run it top to bottom (or with the Collection Runner). It saves `maidId` and `bookingId` automatically and checks the expected status code for each request.

## Key design decisions

- **Layered packages**: controller → service → repository, with the domain model kept free of Spring.
- **Repository interfaces**: storage sits behind `MaidRepository` and `BookingRepository`, so the in-memory versions can be swapped for a database without changing the services.
- **`PaymentProcessor` interface**: `PaymentService` builds a `Map<method, processor>` from all injected beans. Adding a wallet means adding one new `@Component`, with no changes to existing code.
- **`BookingType` decides the slot start**: each type implements `resolveStart(...)`. A new type adds one enum constant and leaves `BookingService` alone.
- **Rich domain model**: `Slot.overlaps()` holds the double-booking rule, `Maid.worksDuring()` checks availability, and `Booking` guards its own state changes (`CREATED → PAID → CANCELLED`).
- **Cancellation frees the slot** because cancelled bookings are skipped in the overlap check (`Booking.blocks()`).
- **Concurrency**: `book()` and `cancel()` are `synchronized`, so the check-then-save step is atomic within a single JVM.

## Assumptions

- A maid has one daily working window, and a booking must fall within a single day inside it.
- Slots are half-open `[start, end)`, so back-to-back bookings (10–11 and 11–12) are allowed.
- Bookings are 1–12 whole hours. Amount = price per hour × hours.
- A booking can be paid only while `CREATED`. A `PAID` or `CREATED` booking can be cancelled. Cancelling twice fails.
- Payment gateways are mocked: UPI needs a `name@bank` ID, and a card needs 16 digits.
- Rating is supplied at onboarding (there are no reviews yet).
- Data is in memory and is lost when the app restarts.

## Future scope

- Recurring bookings (generate N weekly slots), and cancelling a single occurrence or the whole series
- Payment idempotency (an idempotency key per payment request)
- Concurrency that works across instances: per-maid locks, optimistic locking, or a DB unique constraint, since `synchronized` only protects a single JVM
- Availability-aware search (return only maids who are free for a requested time), and a pluggable filter abstraction
