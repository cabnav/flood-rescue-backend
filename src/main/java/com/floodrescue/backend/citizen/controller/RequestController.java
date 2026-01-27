package com.floodrescue.backend.citizen.controller;

import com.floodrescue.backend.citizen.dto.CreateRequestRequest;
import com.floodrescue.backend.citizen.dto.RequestDetailResponse;
import com.floodrescue.backend.citizen.service.RequestService;
import com.floodrescue.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rescue-requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> createRequest(@RequestBody CreateRequestRequest request) {
        RequestDetailResponse response = requestService.createRequest(request);
        return ResponseEntity.ok(ApiResponse.success("Request created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> getRequestById(@PathVariable Integer id) {
        RequestDetailResponse response = requestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<RequestDetailResponse>>> getAllRequests() {
        List<RequestDetailResponse> responses = requestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<RequestDetailResponse>>> getRequestsByUserId(@PathVariable Integer userId) {
        List<RequestDetailResponse> responses = requestService.getRequestsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> updateRequestStatus(
            @PathVariable Integer id,
            @RequestBody String status) {
        RequestDetailResponse response = requestService.updateRequestStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }
}
