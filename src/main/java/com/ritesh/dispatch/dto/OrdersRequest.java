package com.ritesh.dispatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrdersRequest(
        @NotEmpty(message = "orders must contain at least one order") List<@Valid OrderRequest> orders) {
}
