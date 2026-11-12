package com.ritesh.dispatch.service;

import com.ritesh.dispatch.dto.DispatchPlanResponse;
import com.ritesh.dispatch.dto.OrderView;
import com.ritesh.dispatch.dto.UnassignedOrderView;
import com.ritesh.dispatch.dto.VehiclePlan;
import com.ritesh.dispatch.entity.DeliveryOrder;
import com.ritesh.dispatch.service.assignment.AssignmentResult;
import com.ritesh.dispatch.service.assignment.VehicleRoute;
import java.util.List;
import java.util.Locale;

/** Converts internal assignment results into API response objects. */
public final class PlanMapper {

    private PlanMapper() {
    }

    public static DispatchPlanResponse toResponse(AssignmentResult result) {
        List<VehiclePlan> plans = result.routes().stream().map(PlanMapper::toVehiclePlan).toList();
        List<UnassignedOrderView> unassigned = result.unassigned().stream()
                .map(u -> new UnassignedOrderView(toView(u.order()), u.reason()))
                .toList();
        return new DispatchPlanResponse(plans, unassigned);
    }

    private static VehiclePlan toVehiclePlan(VehicleRoute route) {
        return new VehiclePlan(
                route.getVehicle().getVehicleId(),
                route.getLoad(),
                String.format(Locale.ROOT, "%.2f km", route.getDistanceKm()),
                route.getStops().stream().map(PlanMapper::toView).toList());
    }

    private static OrderView toView(DeliveryOrder o) {
        return new OrderView(o.getOrderId(), o.getLatitude(), o.getLongitude(),
                o.getAddress(), o.getPackageWeight(), o.getPriority());
    }
}
