package com.ritesh.dispatch.repository;

import com.ritesh.dispatch.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}
