package com.floodrescue.backend.citizen.controller;

import com.floodrescue.backend.citizen.dto.CreateFeedbackRequest;
import com.floodrescue.backend.citizen.dto.ClassifyRequestRequest;
import com.floodrescue.backend.citizen.dto.CreateRequestRequest;
import com.floodrescue.backend.citizen.dto.FeedbackResponse;
import com.floodrescue.backend.citizen.dto.RequestDetailResponse;
import com.floodrescue.backend.citizen.service.FeedbackService;
import com.floodrescue.backend.citizen.dto.RequestMediaResponse;
import com.floodrescue.backend.citizen.service.RequestMediaService;
import com.floodrescue.backend.citizen.service.RequestService;
import com.floodrescue.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rescue-requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;
    private final FeedbackService feedbackService;
    private final RequestMediaService requestMediaService;

    @PostMapping("/rescue")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> createRescue(
            @Valid @RequestBody CreateRequestRequest request) {
        RequestDetailResponse response = requestService.createRescue(request);
        return ResponseEntity.ok(ApiResponse.success("Request created successfully", response));
    }

    @PostMapping("/relief")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> createRelief(
            @Valid @RequestBody CreateRequestRequest request) {
        RequestDetailResponse response = requestService.createRelief(request);
        return ResponseEntity.ok(ApiResponse.success("Request created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("""
            hasRole('ADMIN') or
            hasRole('RESCUE_COORDINATOR') or
            hasRole('RESCUE_TEAM') or
            (hasRole('CITIZEN') and @requestSecurity.isOwner(#id, authentication.name))""")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> getRequestById(@PathVariable Integer id) {
        RequestDetailResponse response = requestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/feedback")
    @PreAuthorize("hasRole('CITIZEN') and @requestSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<ApiResponse<FeedbackResponse>> createFeedback(
            @PathVariable Integer id,
            @Valid @RequestBody CreateFeedbackRequest request) {
        FeedbackResponse response = feedbackService.createFeedback(id, request);
        return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<RequestDetailResponse>>> getAllRequests() {
        List<RequestDetailResponse> responses = requestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'RESCUE_TEAM', 'CITIZEN')")
    public ResponseEntity<ApiResponse<List<RequestDetailResponse>>> getRequestsByUserId(@PathVariable Integer userId) {
        List<RequestDetailResponse> responses = requestService.getRequestsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'RESCUE_TEAM')")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> updateRequestStatus(
            @PathVariable Integer id,
            @RequestBody String status) {
        RequestDetailResponse response = requestService.updateRequestStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'RESCUE_TEAM')")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> approveRequest(
            @PathVariable Integer id) {
        RequestDetailResponse response = requestService.approveRequestStatus(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/{requestId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RequestMediaResponse>> uploadMedia(
            @PathVariable Integer requestId,
            @RequestParam MultipartFile file) {

        RequestMediaResponse response = requestMediaService.uploadMedia(requestId, file);

        return ResponseEntity.ok(ApiResponse.success("Media uploaded successfully", response));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'RESCUE_TEAM')")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> cancelRequest(
            @PathVariable Integer id) {
        RequestDetailResponse response = requestService.cancelRequestStatus(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/classify")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> classifyRequest(
            @PathVariable Integer id,
            @Valid @RequestBody ClassifyRequestRequest request) {
        RequestDetailResponse response = requestService.classifyRequest(id, request);
        return ResponseEntity.ok(ApiResponse.success("Phân loại yêu cầu thành công", response));
    }

}
