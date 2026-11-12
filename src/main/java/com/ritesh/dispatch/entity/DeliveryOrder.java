package com.ritesh.dispatch.entity;

import com.ritesh.dispatch.model.Priority;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "delivery_orders")
public class DeliveryOrder {

    @Id
    private String orderId;
    private double latitude;
    private double longitude;
    private String address;
    private double packageWeight;
    @Enumerated(EnumType.STRING)
    private Priority priority;

    protected DeliveryOrder() {
    }

    public DeliveryOrder(String orderId, double latitude, double longitude,
                         String address, double packageWeight, Priority priority) {
        this.orderId = orderId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.packageWeight = packageWeight;
        this.priority = priority;
    }

    public String getOrderId() { return orderId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress() { return address; }
    public double getPackageWeight() { return packageWeight; }
    public Priority getPriority() { return priority; }
}
