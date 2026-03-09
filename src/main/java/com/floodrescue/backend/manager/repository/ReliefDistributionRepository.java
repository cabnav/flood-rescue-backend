package com.floodrescue.backend.manager.repository;

import com.floodrescue.backend.manager.model.ReliefDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReliefDistributionRepository extends JpaRepository<ReliefDistribution, Integer> {

    List<ReliefDistribution> findByMission_IdAndReturnedFalse(Integer missionId);
}

