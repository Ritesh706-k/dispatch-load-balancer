package com.ritesh.dispatch.controller;

import com.ritesh.dispatch.dto.ApiResponse;
import com.ritesh.dispatch.dto.DispatchPlanResponse;
import com.ritesh.dispatch.dto.OrdersRequest;
import com.ritesh.dispatch.dto.VehiclesRequest;
import com.ritesh.dispatch.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatch")
@Tag(name = "Dispatch", description = "Submit orders and vehicles, then fetch the optimised dispatch plan")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @Operation(summary = "Submit delivery orders (existing orderIds are updated)")
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse> submitOrders(@Valid @RequestBody OrdersRequest request) {
        dispatchService.saveOrders(request.orders());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Delivery orders accepted."));
    }

    @Operation(summary = "Submit fleet details (existing vehicleIds are updated)")
    @PostMapping("/vehicles")
    public ResponseEntity<ApiResponse> submitVehicles(@Valid @RequestBody VehiclesRequest request) {
        dispatchService.saveVehicles(request.vehicles());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Vehicle details accepted."));
    }

    @Operation(summary = "Get the optimised dispatch plan, including any unassigned orders")
    @GetMapping("/plan")
    public DispatchPlanResponse getPlan() {
        return dispatchService.buildPlan();
    }
}
