package com.ritesh.dispatch.service.assignment;

import com.ritesh.dispatch.entity.DeliveryOrder;
import com.ritesh.dispatch.entity.Vehicle;
import com.ritesh.dispatch.util.HaversineCalculator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Mutable working state of one vehicle while a plan is being built. */
public class VehicleRoute {

    private static final double EPSILON = 1e-9;

    private final Vehicle vehicle;
    private final List<DeliveryOrder> stops = new ArrayList<>();
    private double load;
    private double distanceKm;
    private double currentLat;
    private double currentLon;

    public VehicleRoute(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.currentLat = vehicle.getCurrentLatitude();
        this.currentLon = vehicle.getCurrentLongitude();
    }

    public boolean canCarry(DeliveryOrder order) {
        return load + order.getPackageWeight() <= vehicle.getCapacity() + EPSILON;
    }

    /** Distance from the vehicle's latest position (start or last stop) to the order. */
    public double distanceTo(DeliveryOrder order) {
        return HaversineCalculator.distanceKm(currentLat, currentLon, order.getLatitude(), order.getLongitude());
    }

    public void add(DeliveryOrder order) {
        distanceKm += distanceTo(order);
        load += order.getPackageWeight();
        stops.add(order);
        currentLat = order.getLatitude();
        currentLon = order.getLongitude();
    }

    public Vehicle getVehicle() { return vehicle; }
    public List<DeliveryOrder> getStops() { return Collections.unmodifiableList(stops); }
    public double getLoad() { return load; }
    public double getDistanceKm() { return distanceKm; }
}
