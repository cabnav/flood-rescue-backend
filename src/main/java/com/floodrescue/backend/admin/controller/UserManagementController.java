package com.floodrescue.backend.admin.controller;

import com.floodrescue.backend.admin.dto.UserDetailResponse;
import com.floodrescue.backend.admin.dto.UserStatusUpdateRequest;
import com.floodrescue.backend.admin.service.UserManagementService;
import com.floodrescue.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDetailResponse>>> getAllUsers() {
        List<UserDetailResponse> responses = userManagementService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(@PathVariable Integer id) {
        UserDetailResponse response = userManagementService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDetailResponse>> updateUserStatus(
            @PathVariable Integer id,
            @RequestBody UserStatusUpdateRequest request) {
        UserDetailResponse response = userManagementService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", response));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDetailResponse>> approveUser(@PathVariable Integer id) {
        UserDetailResponse response = userManagementService.approveUser(id);
        return ResponseEntity.ok(ApiResponse.success("User approved successfully", response));
    }
}
