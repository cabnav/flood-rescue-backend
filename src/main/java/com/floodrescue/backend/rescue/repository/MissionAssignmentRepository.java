package com.floodrescue.backend.rescue.repository;

import com.floodrescue.backend.rescue.model.Mission;
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

    List<MissionAssignment> findByMission_Id(Integer missionId);

    List<MissionAssignment> findByStatusAndMission_StatusIn(
            MissionAssignment.AssignmentStatus status,
            List<Mission.MissionStatus> missionStatuses);
}
