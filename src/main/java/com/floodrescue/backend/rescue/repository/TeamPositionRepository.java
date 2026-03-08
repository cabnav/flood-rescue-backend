package com.floodrescue.backend.rescue.repository;

import com.floodrescue.backend.rescue.model.TeamPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamPositionRepository extends JpaRepository<TeamPosition, Integer> {
    Optional<TeamPosition> findTopByTeam_IdOrderByRecordedAtDescPositionIdDesc(Integer teamId);
}

