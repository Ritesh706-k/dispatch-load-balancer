package com.ritesh.dispatch.service.assignment;

import com.ritesh.dispatch.entity.DeliveryOrder;

public record UnassignedItem(DeliveryOrder order, String reason) {
}
