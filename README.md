# Dispatch Load Balancer

Spring Boot service that assigns delivery orders to a fleet of vehicles. It respects vehicle capacity, serves
HIGH priority orders first and keeps travel distance low using the Haversine formula.

## Tech
Java 17, Spring Boot 3.3, Spring Data JPA, H2 (in-memory), springdoc-openapi (Swagger), JUnit 5, Mockito.

## Run locally
```
mvn spring-boot:run
```
- Swagger UI: http://localhost:8080/swagger-ui.html
- Tests: `mvn test`

## API
| Method | Path | Purpose |
|---|---|---|
| POST | `/api/dispatch/orders` | Submit orders (existing `orderId` is updated, not duplicated) |
| POST | `/api/dispatch/vehicles` | Submit vehicles (existing `vehicleId` is updated) |
| GET | `/api/dispatch/plan` | Get the optimised plan plus any `unassignedOrders` |

Call them in that order. Sample bodies are in `samples/`.

Errors return `{ "status": "error", "message": "...", "errors": [...] }`:
400 for validation problems or malformed JSON, 409 if the plan is requested before orders and vehicles exist.

## Algorithm (Strategy pattern)
`AssignmentStrategy` is the extension point; `PriorityNearestVehicleStrategy` is the default:
1. Sort orders HIGH, MEDIUM, LOW (heavier first within a priority).
2. For each order, pick the vehicle that still has capacity and is closest to it, measured from the vehicle's
   latest stop (Haversine).
3. Orders that fit nowhere are returned in `unassignedOrders` with a reason, so LOW priority orders drop first.

`totalDistance` is the sum of legs from the vehicle's start through its stops in order (no return trip).
The sample data has 645 units of weight against 500 of fleet capacity, so unassigned orders are expected.

## Design notes
- Layers: controller, service, repository, entity, dto; global `@RestControllerAdvice` for errors.
- Greedy is fast (O(orders x vehicles)) but not globally optimal; noted as a trade-off.

## Deploy (Render, Docker)
Create a Web Service from this repo, runtime Docker. The app reads the `PORT` env var automatically.
