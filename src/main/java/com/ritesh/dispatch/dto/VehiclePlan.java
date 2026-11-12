package com.ritesh.dispatch.dto;

import java.util.List;

public record VehiclePlan(String vehicleId, double totalLoad, String totalDistance,
                          List<OrderView> assignedOrders) {
}
