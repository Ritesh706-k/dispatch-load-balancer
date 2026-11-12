package com.ritesh.dispatch.dto;

import com.ritesh.dispatch.model.Priority;

public record OrderView(String orderId, double latitude, double longitude,
                        String address, double packageWeight, Priority priority) {
}
