package com.floodrescue.backend.rescue.repository;

import com.floodrescue.backend.rescue.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query("SELECT COALESCE(SUM(r.peopleRescued), 0) FROM Report r")
    Long sumPeopleRescued();

    List<Report> findAllByOrderByCreatedAtDesc();

    List<Report> findByMission_IdInOrderByCreatedAtDesc(List<Integer> missionIds);
}
