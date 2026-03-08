package com.floodrescue.backend.rescue.repository;

import com.floodrescue.backend.rescue.model.TeamPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamPositionRepository extends JpaRepository<TeamPosition, Integer> {
    List<TeamPosition> findByTeamId(Integer teamId);
}
