package com.ritesh.dispatch.service.assignment;

import com.ritesh.dispatch.entity.DeliveryOrder;
import com.ritesh.dispatch.entity.Vehicle;
import java.util.List;

/** Strategy pattern: swap in a different optimiser without touching the service or controller. */
public interface AssignmentStrategy {
    AssignmentResult assign(List<DeliveryOrder> orders, List<Vehicle> vehicles);
}
