package com.floodrescue.backend.citizen.dto;

import com.floodrescue.backend.admin.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    private Integer id;
    private Integer requestId;
    private Integer userId;
    private Integer rating;
    private String comment;
    private Feedback.FeedbackType feedbackType;
    private LocalDateTime createdAt;
}

