package com.ritesh.dispatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ritesh.dispatch.entity.DeliveryOrder;
import com.ritesh.dispatch.entity.Vehicle;
import com.ritesh.dispatch.model.Priority;
import com.ritesh.dispatch.service.assignment.AssignmentResult;
import com.ritesh.dispatch.service.assignment.PriorityNearestVehicleStrategy;
import com.ritesh.dispatch.service.assignment.VehicleRoute;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PriorityNearestVehicleStrategyTest {

    private final PriorityNearestVehicleStrategy strategy = new PriorityNearestVehicleStrategy();

    private static DeliveryOrder order(String id, double lat, double lon, double weight, Priority p) {
        return new DeliveryOrder(id, lat, lon, "addr " + id, weight, p);
    }

    private static Vehicle vehicle(String id, double capacity, double lat, double lon) {
        return new Vehicle(id, capacity, lat, lon, "addr " + id);
    }

    @Test
    void highPriorityIsAssignedBeforeLowWhenCapacityIsTight() {
        List<DeliveryOrder> orders = List.of(
                order("LOW1", 28.6, 77.2, 15, Priority.LOW),
                order("HIGH1", 28.6, 77.2, 15, Priority.HIGH));
        AssignmentResult result = strategy.assign(orders, List.of(vehicle("V1", 20, 28.6, 77.2)));

        assertEquals("HIGH1", result.routes().get(0).getStops().get(0).getOrderId());
        assertEquals(1, result.unassigned().size());
        assertEquals("LOW1", result.unassigned().get(0).order().getOrderId());
    }

    @Test
    void orderGoesToNearestVehicle() {
        Vehicle delhi = vehicle("DELHI", 100, 28.6139, 77.2090);
        Vehicle chennai = vehicle("CHENNAI", 100, 13.0674, 80.2376);
        AssignmentResult result = strategy.assign(
                List.of(order("O1", 13.0827, 80.2707, 10, Priority.MEDIUM)), List.of(delhi, chennai));

        VehicleRoute chennaiRoute = result.routes().stream()
                .filter(r -> r.getVehicle().getVehicleId().equals("CHENNAI")).findFirst().orElseThrow();
        assertEquals(1, chennaiRoute.getStops().size());
    }

    @Test
    void overweightOrderIsReportedUnassigned() {
        AssignmentResult result = strategy.assign(
                List.of(order("BIG", 28.6, 77.2, 500, Priority.HIGH)),
                List.of(vehicle("V1", 100, 28.6, 77.2)));

        assertEquals(1, result.unassigned().size());
        assertTrue(result.unassigned().get(0).reason().contains("exceeds"));
    }

    @Test
    void capacityIsNeverExceededAndLowPriorityIsDroppedFirst() {
        Priority[] cycle = {Priority.HIGH, Priority.MEDIUM, Priority.LOW};
        List<DeliveryOrder> orders = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            orders.add(order("ORD" + i, 28.5 + i * 0.01, 77.1 + i * 0.01, 20, cycle[i % 3]));
        }
        List<Vehicle> fleet = List.of(
                vehicle("V1", 100, 28.7041, 77.1025), vehicle("V2", 80, 28.5355, 77.3910),
                vehicle("V3", 120, 28.4595, 77.0266), vehicle("V4", 90, 28.6139, 77.2090),
                vehicle("V5", 110, 28.7041, 77.1025));

        AssignmentResult result = strategy.assign(orders, fleet);

        result.routes().forEach(r -> assertTrue(r.getLoad() <= r.getVehicle().getCapacity()));
        int assigned = result.routes().stream().mapToInt(r -> r.getStops().size()).sum();
        assertEquals(24, assigned);
        assertEquals(6, result.unassigned().size());
        assertTrue(result.unassigned().stream().allMatch(u -> u.order().getPriority() == Priority.LOW));
    }

    @Test
    void routeDistanceChainsFromStopToStop() {
        AssignmentResult result = strategy.assign(
                List.of(order("A", 0, 1, 10, Priority.HIGH), order("B", 0, 2, 5, Priority.HIGH)),
                List.of(vehicle("V1", 100, 0, 0)));

        // Heavier order A is first: 0->1 degree, then 1->2 degree (~111.19 km each).
        assertEquals(222.39, result.routes().get(0).getDistanceKm(), 0.1);
    }
}
