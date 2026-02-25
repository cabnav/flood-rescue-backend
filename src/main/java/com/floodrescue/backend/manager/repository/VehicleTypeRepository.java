package com.floodrescue.backend.manager.repository;

import com.floodrescue.backend.manager.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Integer> {
    List<VehicleType> findByStatus(String status);
}
