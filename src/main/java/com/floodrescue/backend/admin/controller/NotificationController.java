package com.floodrescue.backend.admin.controller;

import com.floodrescue.backend.admin.dto.NotificationResponse;
import com.floodrescue.backend.admin.service.NotificationService;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CITIZEN','RESCUE_TEAM','RESCUE_COORDINATOR','MANAGER')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        Integer userId = getCurrentUser().getId();
        List<NotificationResponse> responses = notificationService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('ADMIN','CITIZEN','RESCUE_TEAM','RESCUE_COORDINATOR','MANAGER')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyUnreadNotifications() {
        Integer userId = getCurrentUser().getId();
        List<NotificationResponse> responses = notificationService.getUnreadByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN','CITIZEN','RESCUE_TEAM','RESCUE_COORDINATOR','MANAGER')")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable @NotNull Integer id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("User not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}

