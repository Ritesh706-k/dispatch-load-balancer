package com.ritesh.dispatch.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ritesh.dispatch.dto.OrderRequest;
import com.ritesh.dispatch.exception.InvalidRequestException;
import com.ritesh.dispatch.exception.PlanNotReadyException;
import com.ritesh.dispatch.model.Priority;
import com.ritesh.dispatch.repository.OrderRepository;
import com.ritesh.dispatch.repository.VehicleRepository;
import com.ritesh.dispatch.service.assignment.AssignmentStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class DispatchServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final AssignmentStrategy strategy = mock(AssignmentStrategy.class);
    private final DispatchService service = new DispatchService(orderRepository, vehicleRepository, strategy);

    @Test
    void duplicateOrderIdsInOneRequestAreRejected() {
        OrderRequest r = new OrderRequest("ORD1", 12.9, 77.5, "MG Road", 10.0, Priority.HIGH);
        assertThrows(InvalidRequestException.class, () -> service.saveOrders(List.of(r, r)));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void planWithoutOrdersIsNotReady() {
        when(orderRepository.findAll(Sort.by("orderId"))).thenReturn(List.of());
        assertThrows(PlanNotReadyException.class, service::buildPlan);
    }
}
