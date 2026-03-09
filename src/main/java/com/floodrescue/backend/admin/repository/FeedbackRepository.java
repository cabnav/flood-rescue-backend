package com.floodrescue.backend.admin.repository;

import com.floodrescue.backend.admin.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    boolean existsByRequestIdAndUserId(Integer requestId, Integer userId);

    Optional<Feedback> findByRequestIdAndUserId(Integer requestId, Integer userId);

    List<Feedback> findAllByOrderByCreatedAtDesc();

    List<Feedback> findByUser_IdOrderByCreatedAtDesc(Integer userId);
}

