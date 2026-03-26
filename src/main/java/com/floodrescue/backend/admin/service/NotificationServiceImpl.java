package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.NotificationResponse;
import com.floodrescue.backend.admin.model.Notification;
import com.floodrescue.backend.admin.repository.NotificationRepository;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationResponse create(Integer userId, String message) {
        if (userId == null) {
            throw new BadRequestException("User id is required");
        }
        if (message == null || message.isBlank()) {
            throw new BadRequestException("Message is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public NotificationResponse getById(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo với id: " + id));
        authorize(notification);
        return mapToResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getByUserId(Integer userId) {
        authorize(userId);
        return notificationRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadByUserId(Integer userId) {
        authorize(userId);
        return notificationRepository.findByUserIdAndIsRead(userId, false).stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public NotificationResponse markAsRead(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo với id: " + id));
        authorize(notification);
        if (Boolean.TRUE.equals(notification.getIsRead())) {
            return mapToResponse(notification);
        }
        notification.setIsRead(true);
        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUser() != null ? notification.getUser().getId() : null,
                notification.getMessage(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }

    private void authorize(Notification notification) {
        if (notification == null || notification.getUser() == null) {
            return;
        }
        authorize(notification.getUser().getId());
    }

    private void authorize(Integer ownerUserId) {
        User current = getCurrentUser();
        if (current == null || ownerUserId == null) {
            throw new UnauthorizedAccessException("Không được phép truy cập thông báo này");
        }
        boolean isOwner = ownerUserId.equals(current.getId());
        boolean isAdmin = hasRole(current, "ADMIN");
        boolean isCoordinator = hasRole(current, "RESCUE_COORDINATOR");
        if (!isOwner && !isAdmin && !isCoordinator) {
            throw new UnauthorizedAccessException("Không được phép truy cập thông báo này");
        }
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

    private boolean hasRole(User user, String roleName) {
        return user != null
                && user.getRole() != null
                && roleName.equalsIgnoreCase(user.getRole().getName());
    }
}
