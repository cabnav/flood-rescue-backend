package com.floodrescue.backend.rescue.repository;

import com.floodrescue.backend.rescue.model.TeamPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TeamPositionRepository extends JpaRepository<TeamPosition, Integer> {
}
