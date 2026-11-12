package com.ritesh.dispatch.service;

import com.ritesh.dispatch.dto.DispatchPlanResponse;
import com.ritesh.dispatch.dto.OrderRequest;
import com.ritesh.dispatch.dto.VehicleRequest;
import com.ritesh.dispatch.entity.DeliveryOrder;
import com.ritesh.dispatch.entity.Vehicle;
import com.ritesh.dispatch.exception.InvalidRequestException;
import com.ritesh.dispatch.exception.PlanNotReadyException;
import com.ritesh.dispatch.repository.OrderRepository;
import com.ritesh.dispatch.repository.VehicleRepository;
import com.ritesh.dispatch.service.assignment.AssignmentStrategy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DispatchService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final AssignmentStrategy assignmentStrategy;

    public DispatchService(OrderRepository orderRepository,
                           VehicleRepository vehicleRepository,
                           AssignmentStrategy assignmentStrategy) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.assignmentStrategy = assignmentStrategy;
    }

    /** Upsert: an existing orderId is overwritten instead of duplicated. */
    @Transactional
    public int saveOrders(List<OrderRequest> requests) {
        rejectDuplicates(requests.stream().map(OrderRequest::orderId).toList(), "orderId");
        List<DeliveryOrder> entities = requests.stream()
                .map(r -> new DeliveryOrder(r.orderId(), r.latitude(), r.longitude(),
                        r.address(), r.packageWeight(), r.priority()))
                .toList();
        orderRepository.saveAll(entities);
        return entities.size();
    }

    /** Upsert: an existing vehicleId is overwritten instead of duplicated. */
    @Transactional
    public int saveVehicles(List<VehicleRequest> requests) {
        rejectDuplicates(requests.stream().map(VehicleRequest::vehicleId).toList(), "vehicleId");
        List<Vehicle> entities = requests.stream()
                .map(r -> new Vehicle(r.vehicleId(), r.capacity(), r.currentLatitude(),
                        r.currentLongitude(), r.currentAddress()))
                .toList();
        vehicleRepository.saveAll(entities);
        return entities.size();
    }

    @Transactional(readOnly = true)
    public DispatchPlanResponse buildPlan() {
        List<DeliveryOrder> orders = orderRepository.findAll(Sort.by("orderId"));
        List<Vehicle> vehicles = vehicleRepository.findAll(Sort.by("vehicleId"));
        if (orders.isEmpty()) {
            throw new PlanNotReadyException("No orders submitted yet. POST /api/dispatch/orders first.");
        }
        if (vehicles.isEmpty()) {
            throw new PlanNotReadyException("No vehicles submitted yet. POST /api/dispatch/vehicles first.");
        }
        return PlanMapper.toResponse(assignmentStrategy.assign(orders, vehicles));
    }

    private void rejectDuplicates(List<String> ids, String fieldName) {
        Set<String> seen = new HashSet<>();
        for (String id : ids) {
            if (!seen.add(id)) {
                throw new InvalidRequestException("Duplicate " + fieldName + " in request: " + id);
            }
        }
    }
}
