package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.FeedbackAdminResponse;

import java.util.List;

public interface FeedbackAdminService {
    List<FeedbackAdminResponse> getAllFeedbacks();

    List<FeedbackAdminResponse> getFeedbacksByUserId(Integer userId);
}
