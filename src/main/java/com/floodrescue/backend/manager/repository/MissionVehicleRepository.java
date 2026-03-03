package com.floodrescue.backend.manager.repository;

import com.floodrescue.backend.manager.model.MissionVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionVehicleRepository extends JpaRepository<MissionVehicle, Integer> {
    List<MissionVehicle> findByMissionId(Integer missionId);

    List<MissionVehicle> findByVehicleVehicleId(Integer vehicleId);
}
