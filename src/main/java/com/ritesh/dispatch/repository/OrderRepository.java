package com.ritesh.dispatch.repository;

import com.ritesh.dispatch.entity.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<DeliveryOrder, String> {
}
