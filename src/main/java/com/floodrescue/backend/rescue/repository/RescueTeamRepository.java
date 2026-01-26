package com.floodrescue.backend.rescue.repository;

import com.floodrescue.backend.rescue.model.RescueTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RescueTeamRepository extends JpaRepository<RescueTeam, Integer> {
    List<RescueTeam> findByStatus(String status);
}
