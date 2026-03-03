package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.admin.model.Feedback;
import com.floodrescue.backend.admin.repository.FeedbackRepository;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.citizen.dto.CreateFeedbackRequest;
import com.floodrescue.backend.citizen.dto.FeedbackResponse;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.InvalidRequestStatusException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.exception.UnauthorizedAccessException;
import com.floodrescue.backend.rescue.model.Mission;
import com.floodrescue.backend.rescue.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;

    @Override
    @Transactional
    public FeedbackResponse createFeedback(Integer requestId, CreateFeedbackRequest request) {
        if (requestId == null) {
            throw new BadRequestException("Request ID is required");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Người dùng chưa được xác thực");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Request sosRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với id: " + requestId));

        if (!sosRequest.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Bạn không có quyền đánh giá yêu cầu này");
        }

        if (sosRequest.getStatus() != Request.RequestStatus.COMPLETED) {
            throw new InvalidRequestStatusException("Chỉ có thể đánh giá khi yêu cầu ở trạng thái COMPLETED");
        }

        // Optional: if a Mission exists, ensure it is completed too.
        missionRepository.findByRequest_Id(requestId).ifPresent(mission -> {
            if (mission.getStatus() != Mission.MissionStatus.COMPLETED) {
                throw new InvalidRequestStatusException("Chỉ có thể đánh giá khi nhiệm vụ đã hoàn thành");
            }
        });

        Integer rating = request != null ? request.getRating() : null;
        if (rating == null || rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        if (feedbackRepository.existsByRequestIdAndUserId(requestId, user.getId())) {
            throw new BadRequestException("Bạn đã gửi đánh giá cho yêu cầu này");
        }

        Feedback.FeedbackType feedbackType = mapFeedbackType(sosRequest.getRequestType());

        Feedback feedback = new Feedback();
        feedback.setRequest(sosRequest);
        feedback.setUser(user);
        feedback.setRating(rating);
        feedback.setComment(request.getComment());
        feedback.setFeedbackType(feedbackType);

        Feedback saved = feedbackRepository.save(feedback);
        return mapToResponse(saved);
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getRequest().getId(),
                feedback.getUser().getId(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getFeedbackType(),
                feedback.getCreatedAt());
    }

    private Feedback.FeedbackType mapFeedbackType(Request.RequestType requestType) {
        if (requestType == null) {
            throw new BadRequestException("Request type is required");
        }
        return switch (requestType) {
            case RESCUE -> Feedback.FeedbackType.RESCUE;
            case RELIEF -> Feedback.FeedbackType.RELIEF;
            case OTHER -> throw new BadRequestException("Không hỗ trợ đánh giá cho loại yêu cầu OTHER");
        };
    }
}

