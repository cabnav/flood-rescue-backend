package com.floodrescue.backend.rescue.repository;

import com.floodrescue.backend.rescue.model.MissionAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionAssignmentRepository extends JpaRepository<MissionAssignment, Integer> {

    List<MissionAssignment> findByRescueTeam_Id(Integer rescueTeamId);

    List<MissionAssignment> findByRescueTeam_IdAndStatus(
            Integer rescueTeamId,
            MissionAssignment.AssignmentStatus status
    );
}

