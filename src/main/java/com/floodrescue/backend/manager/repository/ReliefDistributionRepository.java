package com.floodrescue.backend.manager.repository;

import com.floodrescue.backend.manager.model.ReliefDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReliefDistributionRepository extends JpaRepository<ReliefDistribution, Integer> {
}

