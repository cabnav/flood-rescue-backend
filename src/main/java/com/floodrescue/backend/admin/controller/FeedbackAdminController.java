package com.floodrescue.backend.admin.controller;

import com.floodrescue.backend.admin.dto.FeedbackAdminResponse;
import com.floodrescue.backend.admin.service.FeedbackAdminService;
import com.floodrescue.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackAdminController {

    private final FeedbackAdminService feedbackAdminService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackAdminResponse>>> getAllFeedbacks() {
        List<FeedbackAdminResponse> responses = feedbackAdminService.getAllFeedbacks();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackAdminResponse>>> getFeedbacksByUserId(
            @PathVariable Integer userId) {
        List<FeedbackAdminResponse> responses = feedbackAdminService.getFeedbacksByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
