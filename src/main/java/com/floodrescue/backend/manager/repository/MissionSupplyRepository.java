package com.floodrescue.backend.manager.repository;

import com.floodrescue.backend.manager.model.MissionSupply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionSupplyRepository extends JpaRepository<MissionSupply, Integer> {
    List<MissionSupply> findByMissionId(Integer missionId);

    List<MissionSupply> findByMission_IdAndReturnedFalse(Integer missionId);
}
