package com.ritesh.dispatch.service.assignment;

import com.ritesh.dispatch.entity.DeliveryOrder;
import com.ritesh.dispatch.entity.Vehicle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Greedy optimiser:
 * 1. Orders are processed HIGH -> MEDIUM -> LOW (heavier first inside a priority, which packs better).
 * 2. Each order goes to the vehicle that can still carry it and adds the least travel distance
 *    (distance measured from the vehicle's latest stop, using Haversine).
 * 3. Orders that fit nowhere are reported as unassigned with a reason, so LOW orders are dropped first.
 */
@Component
public class PriorityNearestVehicleStrategy implements AssignmentStrategy {

    private static final Comparator<DeliveryOrder> PROCESSING_ORDER =
            Comparator.comparing(DeliveryOrder::getPriority)
                    .thenComparing(Comparator.comparingDouble(DeliveryOrder::getPackageWeight).reversed())
                    .thenComparing(DeliveryOrder::getOrderId);

    @Override
    public AssignmentResult assign(List<DeliveryOrder> orders, List<Vehicle> vehicles) {
        List<VehicleRoute> routes = vehicles.stream().map(VehicleRoute::new).toList();
        double maxCapacity = vehicles.stream().mapToDouble(Vehicle::getCapacity).max().orElse(0);
        List<UnassignedItem> unassigned = new ArrayList<>();

        for (DeliveryOrder order : orders.stream().sorted(PROCESSING_ORDER).toList()) {
            Optional<VehicleRoute> best = routes.stream()
                    .filter(route -> route.canCarry(order))
                    .min(Comparator.comparingDouble((VehicleRoute r) -> r.distanceTo(order))
                            .thenComparing(r -> r.getVehicle().getVehicleId()));

            if (best.isPresent()) {
                best.get().add(order);
            } else {
                unassigned.add(new UnassignedItem(order, reasonFor(order, maxCapacity, vehicles.isEmpty())));
            }
        }
        return new AssignmentResult(routes, unassigned);
    }

    private String reasonFor(DeliveryOrder order, double maxCapacity, boolean noVehicles) {
        if (noVehicles) {
            return "No vehicles available";
        }
        if (order.getPackageWeight() > maxCapacity) {
            return "Package weight exceeds the capacity of every vehicle";
        }
        return "No vehicle has enough remaining capacity";
    }
}
