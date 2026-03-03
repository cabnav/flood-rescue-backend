package com.floodrescue.backend.rescue.repository;

import com.floodrescue.backend.rescue.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Integer> {
    List<Mission> findByStatus(String status);
    List<Mission> findByMissionType(Mission.MissionType missionType);

    Optional<Mission> findByRequest_Id(Integer requestId);
}
