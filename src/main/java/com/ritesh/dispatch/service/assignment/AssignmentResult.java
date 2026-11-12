package com.ritesh.dispatch.service.assignment;

import java.util.List;

public record AssignmentResult(List<VehicleRoute> routes, List<UnassignedItem> unassigned) {
}
