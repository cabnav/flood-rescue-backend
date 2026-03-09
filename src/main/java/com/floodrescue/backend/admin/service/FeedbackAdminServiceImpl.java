package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.FeedbackAdminResponse;
import com.floodrescue.backend.admin.model.Feedback;
import com.floodrescue.backend.admin.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackAdminServiceImpl implements FeedbackAdminService {

    private final FeedbackRepository feedbackRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAdminResponse> getAllFeedbacks() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAdminResponse> getFeedbacksByUserId(Integer userId) {
        return feedbackRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FeedbackAdminResponse mapToResponse(Feedback feedback) {
        return new FeedbackAdminResponse(
                feedback.getId(),
                feedback.getRequest().getId(),
                feedback.getUser().getFullName(),
                feedback.getComment(),
                feedback.getCreatedAt()
        );
    }
}
