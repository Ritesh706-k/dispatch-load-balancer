package com.ritesh.dispatch.dto;

import java.util.List;

public record DispatchPlanResponse(List<VehiclePlan> dispatchPlan,
                                   List<UnassignedOrderView> unassignedOrders) {
}
