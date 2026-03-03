package com.floodrescue.backend.citizen.service;

import com.floodrescue.backend.citizen.dto.CreateFeedbackRequest;
import com.floodrescue.backend.citizen.dto.FeedbackResponse;

public interface FeedbackService {
    FeedbackResponse createFeedback(Integer requestId, CreateFeedbackRequest request);
}

