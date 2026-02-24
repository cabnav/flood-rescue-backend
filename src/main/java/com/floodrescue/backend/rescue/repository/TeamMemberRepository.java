package com.floodrescue.backend.rescue.repository;

import com.floodrescue.backend.rescue.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {

    Optional<TeamMember> findFirstByUser_Id(Integer userId);
}

